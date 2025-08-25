package com.runtracker.domain.community.enums;

import com.runtracker.global.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommunityErrorCode implements ResponseCode {

    POST_NOT_FOUND("CM001", "Post not found"),
    POST_CREATION_FAILED("CM002", "Failed to create post"),
    UNAUTHORIZED_POST_ACCESS("CM003", "Unauthorized access to post");

    private final String statusCode;
    private final String message;
}