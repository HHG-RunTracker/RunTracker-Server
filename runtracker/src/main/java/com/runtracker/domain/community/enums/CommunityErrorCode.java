package com.runtracker.domain.community.enums;

import com.runtracker.global.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommunityErrorCode implements ResponseCode {

    POST_NOT_FOUND("CM001", "Post not found"),
    POST_CREATION_FAILED("CM002", "Failed to create post"),
    UNAUTHORIZED_POST_ACCESS("CM003", "Unauthorized access to post"),
    ALREADY_LIKED_POST("CM004", "Already liked this post"),
    NOT_LIKED_POST("CM005", "Not liked this post"),
    COMMENT_NOT_FOUND("CM006", "PostComment not found"),
    COMMENT_CREATION_FAILED("CM007", "Failed to create comment"),
    UNAUTHORIZED_COMMENT_ACCESS("CM008", "Unauthorized access to comment"),
    NO_POSTS_FOUND("CM009", "No posts found in this crew"),
    NO_SEARCH_RESULTS("CM010", "No posts found for the given keyword");

    private final String statusCode;
    private final String message;
}