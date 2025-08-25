package com.runtracker.domain.community.service;

import com.runtracker.domain.community.dto.PostCreateDTO;
import com.runtracker.domain.community.dto.PostUpdateDTO;
import com.runtracker.domain.community.entity.Post;
import com.runtracker.domain.community.exception.PostCreationFailedException;
import com.runtracker.domain.community.exception.PostNotFoundException;
import com.runtracker.domain.community.exception.UnauthorizedPostAccessException;
import com.runtracker.domain.community.repository.PostRepository;
import com.runtracker.global.security.CrewAuthorizationUtil;
import com.runtracker.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final CrewAuthorizationUtil crewAuthorizationUtil;

    @Transactional
    public void createPost(Long crewId, PostCreateDTO postCreateDTO, UserDetailsImpl userDetails) {
        crewAuthorizationUtil.validateCrewMemberAccess(userDetails, crewId);

        try {
            Post post = Post.builder()
                    .memberId(userDetails.getMemberId())
                    .crewId(crewId)
                    .title(postCreateDTO.getTitle())
                    .content(postCreateDTO.getContent())
                    .photos(postCreateDTO.getPhotos())
                    .build();

            postRepository.save(post);
        } catch (Exception e) {
            throw new PostCreationFailedException();
        }
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateDTO postUpdateDTO, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        crewAuthorizationUtil.validateCrewMemberAccess(userDetails, post.getCrewId());

        if (!post.getMemberId().equals(userDetails.getMemberId())) {
            throw new UnauthorizedPostAccessException();
        }

        if (postUpdateDTO.getTitle() != null) {
            post.updateTitle(postUpdateDTO.getTitle());
        }
        if (postUpdateDTO.getContent() != null) {
            post.updateContent(postUpdateDTO.getContent());
        }
        if (postUpdateDTO.getPhotos() != null) {
            post.updatePhotos(postUpdateDTO.getPhotos());
        }
    }

    @Transactional
    public void deletePost(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        crewAuthorizationUtil.validateCrewMemberAccess(userDetails, post.getCrewId());

        if (!post.getMemberId().equals(userDetails.getMemberId())) {
            throw new UnauthorizedPostAccessException();
        }

        postRepository.deleteById(postId);
    }
}