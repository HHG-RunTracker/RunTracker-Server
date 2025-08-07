package com.runtracker.global.jwt.exception;

import com.runtracker.global.exception.CustomException;
import com.runtracker.global.jwt.enums.JwtErrorCode;

public class UnsupportedJwtTokenException extends CustomException {
    public UnsupportedJwtTokenException() {
        super(JwtErrorCode.UNSUPPORTED_JWT_TOKEN);
    }
}