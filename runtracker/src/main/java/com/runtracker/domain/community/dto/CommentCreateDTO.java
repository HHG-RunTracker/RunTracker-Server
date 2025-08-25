package com.runtracker.domain.community.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentCreateDTO {
    private String comment;
}