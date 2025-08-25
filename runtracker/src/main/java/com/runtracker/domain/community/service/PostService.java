package com.runtracker.domain.community.service;

import com.runtracker.domain.community.dto.CommentCreateDTO;
import com.runtracker.domain.community.dto.CommentUpdateDTO;
import com.runtracker.domain.community.dto.PostCreateDTO;
import com.runtracker.domain.community.dto.PostUpdateDTO;
import com.runtracker.domain.community.entity.Post;
import com.runtracker.domain.community.entity.PostComment;
import com.runtracker.domain.community.entity.PostLike;
import com.runtracker.domain.community.exception.AlreadyLikedPostException;
import com.runtracker.domain.community.exception.CommentCreationFailedException;
import com.runtracker.domain.community.exception.CommentNotFoundException;
import com.runtracker.domain.community.exception.NotLikedPostException;
import com.runtracker.domain.community.exception.PostCreationFailedException;
import com.runtracker.domain.community.exception.PostNotFoundException;
import com.runtracker.domain.community.exception.UnauthorizedCommentAccessException;
import com.runtracker.domain.community.exception.UnauthorizedPostAccessException;
import com.runtracker.domain.community.repository.CommentRepository;
import com.runtracker.domain.community.repository.PostLikeRepository;
import com.runtracker.domain.community.repository.PostRepository;
import com.runtracker.global.security.CrewAuthorizationUtil;
import com.runtracker.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
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

    @Transactional
    public void likePost(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        crewAuthorizationUtil.validateCrewMemberAccess(userDetails, post.getCrewId());

        Long memberId = userDetails.getMemberId();

        if (postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new AlreadyLikedPostException();
        }

        PostLike postLike = PostLike.builder()
                .postId(postId)
                .memberId(memberId)
                .build();
        postLikeRepository.save(postLike);
    }

    @Transactional
    public void unlikePost(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        crewAuthorizationUtil.validateCrewMemberAccess(userDetails, post.getCrewId());

        Long memberId = userDetails.getMemberId();

        if (!postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new NotLikedPostException();
        }

        postLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
    }

    @Transactional
    public void createComment(Long postId, CommentCreateDTO commentCreateDTO, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        crewAuthorizationUtil.validateCrewMemberAccess(userDetails, post.getCrewId());

        try {
            PostComment comment = PostComment.builder()
                    .postId(postId)
                    .memberId(userDetails.getMemberId())
                    .comment(commentCreateDTO.getComment())
                    .build();

            commentRepository.save(comment);
        } catch (Exception e) {
            throw new CommentCreationFailedException();
        }
    }

    @Transactional
    public void updateComment(Long commentId, CommentUpdateDTO commentUpdateDTO, UserDetailsImpl userDetails) {
        PostComment comment = validateCommentAccess(commentId, userDetails);

        if (commentUpdateDTO.getComment() != null) {
            comment.updateComment(commentUpdateDTO.getComment());
        }
    }

    @Transactional
    public void deleteComment(Long commentId, UserDetailsImpl userDetails) {
        validateCommentAccess(commentId, userDetails);
        commentRepository.deleteById(commentId);
    }

    private PostComment validateCommentAccess(Long commentId, UserDetailsImpl userDetails) {
        PostComment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        Post post = postRepository.findById(comment.getPostId())
                .orElseThrow(PostNotFoundException::new);

        crewAuthorizationUtil.validateCrewMemberAccess(userDetails, post.getCrewId());

        if (!comment.getMemberId().equals(userDetails.getMemberId())) {
            throw new UnauthorizedCommentAccessException();
        }

        return comment;
    }
}