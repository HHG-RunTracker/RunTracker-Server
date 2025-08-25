package com.runtracker.domain.community.service;

import com.runtracker.domain.community.dto.PostCreateDTO;
import com.runtracker.domain.community.entity.Post;
import com.runtracker.domain.community.exception.PostCreationFailedException;
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
}