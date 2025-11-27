package com.runtracker.domain.community.exception;

import com.runtracker.domain.community.enums.CommunityErrorCode;
import com.runtracker.global.exception.CustomException;

public class NotLikedPostException extends CustomException {
    public NotLikedPostException() {
        super(CommunityErrorCode.NOT_LIKED_POST);
    }

    public NotLikedPostException(String message) {
        super(CommunityErrorCode.NOT_LIKED_POST, message);
    }
}