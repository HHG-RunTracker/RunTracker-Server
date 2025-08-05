package com.runtracker.domain.auth.exception;

import com.runtracker.domain.auth.enums.AuthErrorCode;
import com.runtracker.global.exception.CustomException;

public class TokenRefreshFailedException extends CustomException {

    public TokenRefreshFailedException() {
        super(AuthErrorCode.TOKEN_REFRESH_FAILED);
    }

    public TokenRefreshFailedException(String message) {
        super(AuthErrorCode.TOKEN_REFRESH_FAILED, message);
    }
}