package com.runtracker.domain.community.exception;

import com.runtracker.domain.community.enums.CommunityErrorCode;
import com.runtracker.global.exception.CustomException;

public class UnauthorizedPostAccessException extends CustomException {
    public UnauthorizedPostAccessException() {
        super(CommunityErrorCode.UNAUTHORIZED_POST_ACCESS);
    }

    public UnauthorizedPostAccessException(String message) {
        super(CommunityErrorCode.UNAUTHORIZED_POST_ACCESS, message);
    }
}