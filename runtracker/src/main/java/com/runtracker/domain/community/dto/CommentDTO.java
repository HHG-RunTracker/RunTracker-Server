package com.runtracker.domain.community.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentDTO {
    private String comment;
}