package com.runtracker.domain.community.exception;

import com.runtracker.domain.community.enums.CommunityErrorCode;
import com.runtracker.global.exception.CustomException;

public class PostCreationFailedException extends CustomException {
    public PostCreationFailedException() {
        super(CommunityErrorCode.POST_CREATION_FAILED);
    }

    public PostCreationFailedException(String message) {
        super(CommunityErrorCode.POST_CREATION_FAILED, message);
    }
}