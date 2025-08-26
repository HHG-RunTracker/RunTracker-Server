package com.runtracker.domain.community.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostDetailDTO {
    private Long postId;
    private String title;
    private String content;
    private List<String> photos;
    private Long memberId;
    private String memberName;
    private Long likeCount;
    private Boolean isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentInfoDTO> comments;
}