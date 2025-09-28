package com.runtracker.domain.community.service;

import com.runtracker.domain.community.dto.CommentDTO;
import com.runtracker.domain.community.dto.CommentInfoDTO;
import com.runtracker.domain.community.dto.PostDTO;
import com.runtracker.domain.community.dto.PostDetailDTO;
import com.runtracker.domain.community.dto.PostListDTO;
import com.runtracker.domain.community.dto.RunningMetaDTO;
import com.runtracker.domain.community.event.PostLikeEvent;
import com.runtracker.domain.community.entity.Post;
import com.runtracker.domain.community.entity.PostComment;
import com.runtracker.domain.community.entity.PostLike;
import com.runtracker.domain.community.exception.AlreadyLikedPostException;
import com.runtracker.domain.community.exception.CommentCreationFailedException;
import com.runtracker.domain.community.exception.CommentNotFoundException;
import com.runtracker.domain.community.exception.NoPostsFoundException;
import com.runtracker.domain.community.exception.NoSearchResultsException;
import com.runtracker.domain.community.exception.NotLikedPostException;
import com.runtracker.domain.community.exception.PostCreationFailedException;
import com.runtracker.domain.community.exception.PostNotFoundException;
import com.runtracker.domain.community.exception.UnauthorizedCommentAccessException;
import com.runtracker.domain.community.exception.UnauthorizedPostAccessException;
import com.runtracker.domain.community.repository.CommentRepository;
import com.runtracker.domain.community.repository.PostLikeRepository;
import com.runtracker.domain.community.repository.PostRepository;
import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.repository.MemberRepository;
import com.runtracker.global.security.CrewAuthorizationUtil;
import com.runtracker.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final CrewAuthorizationUtil crewAuthorizationUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createPost(PostDTO postDTO, UserDetailsImpl userDetails) {
        Long crewId = userDetails.getCrewMembership().getCrewId();
        crewAuthorizationUtil.validateCrewMemberAccess(userDetails, crewId);

        try {
            Post.PostBuilder postBuilder = Post.builder()
                    .memberId(userDetails.getMemberId())
                    .crewId(crewId)
                    .title(postDTO.getTitle())
                    .content(postDTO.getContent())
                    .photos(postDTO.getPhotos());

            if (postDTO.getMeta() != null) {
                postBuilder.distance(postDTO.getMeta().getDistance())
                        .time(postDTO.getMeta().getTime())
                        .avgPace(postDTO.getMeta().getAvgPace())
                        .avgSpeed(postDTO.getMeta().getAvgSpeed());
            }

            Post post = postBuilder.build();

            postRepository.save(post);
        } catch (Exception e) {
            throw new PostCreationFailedException();
        }
    }

    @Transactional
    public void updatePost(Long postId, PostDTO postDTO, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        crewAuthorizationUtil.validateCrewMemberAccess(userDetails, post.getCrewId());

        if (!post.getMemberId().equals(userDetails.getMemberId())) {
            throw new UnauthorizedPostAccessException();
        }

        if (postDTO.getTitle() != null) {
            post.updateTitle(postDTO.getTitle());
        }
        if (postDTO.getContent() != null) {
            post.updateContent(postDTO.getContent());
        }
        if (postDTO.getPhotos() != null) {
            post.updatePhotos(postDTO.getPhotos());
        }
        if (postDTO.getMeta() != null) {
            post.updateRunningMeta(
                    postDTO.getMeta().getDistance(),
                    postDTO.getMeta().getTime(),
                    postDTO.getMeta().getAvgPace(),
                    postDTO.getMeta().getAvgSpeed()
            );
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

        eventPublisher.publishEvent(new PostLikeEvent(memberId, post.getMemberId(), postId));
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
    public void createComment(Long postId, CommentDTO commentDTO, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        crewAuthorizationUtil.validateCrewMemberAccess(userDetails, post.getCrewId());

        try {
            PostComment comment = PostComment.builder()
                    .postId(postId)
                    .memberId(userDetails.getMemberId())
                    .comment(commentDTO.getComment())
                    .build();

            commentRepository.save(comment);
        } catch (Exception e) {
            throw new CommentCreationFailedException();
        }
    }

    @Transactional
    public void updateComment(Long commentId, CommentDTO commentDTO, UserDetailsImpl userDetails) {
        PostComment comment = validateCommentAccess(commentId, userDetails);

        if (commentDTO.getComment() != null) {
            comment.updateComment(commentDTO.getComment());
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

    public List<PostListDTO> getPostList(UserDetailsImpl userDetails) {
        Long crewId = userDetails.getCrewMembership().getCrewId();
        crewAuthorizationUtil.validateCrewMemberAccess(userDetails, crewId);

        List<Post> posts = postRepository.findByCrewIdOrderByCreatedAtDesc(crewId);
        if (posts.isEmpty()) {
            throw new NoPostsFoundException();
        }
        return convertToPostListDTO(posts, userDetails.getMemberId());
    }

    public PostDetailDTO getPostDetail(Long postId, UserDetailsImpl userDetails) {
        Long crewId = userDetails.getCrewMembership().getCrewId();
        crewAuthorizationUtil.validateCrewMemberAccess(userDetails, crewId);

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        if (!post.getCrewId().equals(crewId)) {
            throw new PostNotFoundException();
        }
        
        String memberName = memberRepository.findById(post.getMemberId())
                .map(Member::getName)
                .orElse("Unknown");
        
        long likeCount = postLikeRepository.countLikesByPostId(postId);
        boolean isLiked = postLikeRepository.existsByPostIdAndMemberId(postId, userDetails.getMemberId());
        
        List<PostComment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        List<CommentInfoDTO> commentDTOs = comments.stream()
                .map(comment -> {
                    String commentMemberName = memberRepository.findById(comment.getMemberId())
                            .map(Member::getName)
                            .orElse("Unknown");
                    
                    return CommentInfoDTO.builder()
                            .commentId(comment.getCommentId())
                            .comment(comment.getComment())
                            .memberId(comment.getMemberId())
                            .memberName(commentMemberName)
                            .createdAt(comment.getCreatedAt())
                            .updatedAt(comment.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());
        
        RunningMetaDTO runningMeta = RunningMetaDTO.builder()
                .distance(post.getDistance())
                .time(post.getTime())
                .avgPace(post.getAvgPace())
                .avgSpeed(post.getAvgSpeed())
                .build();

        return PostDetailDTO.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .photos(post.getPhotos())
                .meta(runningMeta)
                .memberId(post.getMemberId())
                .memberName(memberName)
                .likeCount(likeCount)
                .isLiked(isLiked)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .comments(commentDTOs)
                .build();
    }

    public List<PostListDTO> searchPosts(String keyword, UserDetailsImpl userDetails) {
        Long crewId = userDetails.getCrewMembership().getCrewId();
        crewAuthorizationUtil.validateCrewMemberAccess(userDetails, crewId);

        List<Post> posts = postRepository.findByCrewIdAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(crewId, keyword);
        if (posts.isEmpty()) {
            throw new NoSearchResultsException();
        }
        return convertToPostListDTO(posts, userDetails.getMemberId());
    }

    private List<PostListDTO> convertToPostListDTO(List<Post> posts, Long currentMemberId) {
        return posts.stream()
                .map(post -> {
                    String memberName = memberRepository.findById(post.getMemberId())
                            .map(Member::getName)
                            .orElse("Unknown");
                    
                    long likeCount = postLikeRepository.countLikesByPostId(post.getId());
                    long commentCount = commentRepository.countByPostId(post.getId());
                    boolean isLiked = postLikeRepository.existsByPostIdAndMemberId(post.getId(), currentMemberId);
                    
                    RunningMetaDTO runningMeta = RunningMetaDTO.builder()
                            .distance(post.getDistance())
                            .time(post.getTime())
                            .avgPace(post.getAvgPace())
                            .avgSpeed(post.getAvgSpeed())
                            .build();

                    return PostListDTO.builder()
                            .postId(post.getId())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .photos(post.getPhotos())
                            .meta(runningMeta)
                            .memberId(post.getMemberId())
                            .memberName(memberName)
                            .likeCount(likeCount)
                            .commentCount(commentCount)
                            .isLiked(isLiked)
                            .createdAt(post.getCreatedAt())
                            .updatedAt(post.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }
}