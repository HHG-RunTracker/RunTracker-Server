package com.runtracker.domain.community.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostListDTO {
    private Long postId;
    private String title;
    private String content;
    private List<String> photos;
    private Long memberId;
    private String memberName;
    private Long likeCount;
    private Long commentCount;
    private Boolean isLiked;
    private LocalDateTime createdAt;
}