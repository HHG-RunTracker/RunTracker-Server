package com.runtracker.domain.community.exception;

import com.runtracker.domain.community.enums.CommunityErrorCode;
import com.runtracker.global.exception.CustomException;

public class CommentNotFoundException extends CustomException {
    public CommentNotFoundException() {
        super(CommunityErrorCode.COMMENT_NOT_FOUND);
    }

    public CommentNotFoundException(String message) {
        super(CommunityErrorCode.COMMENT_NOT_FOUND, message);
    }
}