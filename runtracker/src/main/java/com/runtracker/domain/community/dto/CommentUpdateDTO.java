package com.runtracker.domain.community.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentUpdateDTO {
    private String comment;
}