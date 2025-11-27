package com.runtracker.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.runtracker.global.code.DateConstants;
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
    private RunningMetaDTO meta;
    private Long memberId;
    private String memberName;
    private Long likeCount;
    private Boolean isLiked;
    @JsonFormat(pattern = DateConstants.DATETIME_PATTERN)
    private LocalDateTime createdAt;
    @JsonFormat(pattern = DateConstants.DATETIME_PATTERN)
    private LocalDateTime updatedAt;
    private List<CommentInfoDTO> comments;
}