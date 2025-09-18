package com.runtracker.domain.community.exception;

import com.runtracker.domain.community.enums.CommunityErrorCode;
import com.runtracker.global.exception.CustomException;

public class NoPostsFoundException extends CustomException {
    public NoPostsFoundException() {
        super(CommunityErrorCode.NO_POSTS_FOUND);
    }

    public NoPostsFoundException(String message) {
        super(CommunityErrorCode.NO_POSTS_FOUND, message);
    }
}