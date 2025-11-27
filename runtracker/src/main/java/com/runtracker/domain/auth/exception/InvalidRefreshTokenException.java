package com.runtracker.domain.auth.exception;

import com.runtracker.domain.auth.enums.AuthErrorCode;
import com.runtracker.global.exception.CustomException;

public class InvalidRefreshTokenException extends CustomException {

    public InvalidRefreshTokenException() {
        super(AuthErrorCode.INVALID_REFRESH_TOKEN);
    }

    public InvalidRefreshTokenException(String message) {
        super(AuthErrorCode.INVALID_REFRESH_TOKEN, message);
    }
}