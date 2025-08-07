package com.runtracker.global.jwt.exception;

import com.runtracker.global.exception.CustomException;
import com.runtracker.global.jwt.enums.JwtErrorCode;

public class InvalidJwtTokenException extends CustomException {
    public InvalidJwtTokenException() {
        super(JwtErrorCode.INVALID_JWT_TOKEN);
    }
}