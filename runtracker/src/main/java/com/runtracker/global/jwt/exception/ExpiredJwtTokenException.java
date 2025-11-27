package com.runtracker.global.jwt.exception;

import com.runtracker.global.exception.CustomException;
import com.runtracker.global.jwt.enums.JwtErrorCode;

public class ExpiredJwtTokenException extends CustomException {
    public ExpiredJwtTokenException() {
        super(JwtErrorCode.EXPIRED_JWT_TOKEN);
    }
}