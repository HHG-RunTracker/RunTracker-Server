package com.runtracker.domain.community.exception;

import com.runtracker.domain.community.enums.CommunityErrorCode;
import com.runtracker.global.exception.CustomException;

public class AlreadyLikedPostException extends CustomException {
    public AlreadyLikedPostException() {
        super(CommunityErrorCode.ALREADY_LIKED_POST);
    }

    public AlreadyLikedPostException(String message) {
        super(CommunityErrorCode.ALREADY_LIKED_POST, message);
    }
}