package com.runtracker.domain.community.exception;

import com.runtracker.domain.community.enums.CommunityErrorCode;
import com.runtracker.global.exception.CustomException;

public class PostNotFoundException extends CustomException {
    public PostNotFoundException() {
        super(CommunityErrorCode.POST_NOT_FOUND);
    }

    public PostNotFoundException(String message) {
        super(CommunityErrorCode.POST_NOT_FOUND, message);
    }
}