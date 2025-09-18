package com.runtracker.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.runtracker.global.code.DateConstants;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentInfoDTO {
    private Long commentId;
    private String comment;
    private Long memberId;
    private String memberName;
    @JsonFormat(pattern = DateConstants.DATETIME_PATTERN)
    private LocalDateTime createdAt;
    @JsonFormat(pattern = DateConstants.DATETIME_PATTERN)
    private LocalDateTime updatedAt;
}