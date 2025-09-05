package com.runtracker.domain.community.exception;

import com.runtracker.domain.community.enums.CommunityErrorCode;
import com.runtracker.global.exception.CustomException;

public class CommentCreationFailedException extends CustomException {
    public CommentCreationFailedException() {
        super(CommunityErrorCode.COMMENT_CREATION_FAILED);
    }

    public CommentCreationFailedException(String message) {
        super(CommunityErrorCode.COMMENT_CREATION_FAILED, message);
    }
}