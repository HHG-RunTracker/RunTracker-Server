package com.runtracker.domain.community.exception;

import com.runtracker.domain.community.enums.CommunityErrorCode;
import com.runtracker.global.exception.CustomException;

public class UnauthorizedCommentAccessException extends CustomException {
    public UnauthorizedCommentAccessException() {
        super(CommunityErrorCode.UNAUTHORIZED_COMMENT_ACCESS);
    }

    public UnauthorizedCommentAccessException(String message) {
        super(CommunityErrorCode.UNAUTHORIZED_COMMENT_ACCESS, message);
    }
}