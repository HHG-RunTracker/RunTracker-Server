package com.runtracker.global.jwt.exception;

import com.runtracker.global.exception.CustomException;
import com.runtracker.global.jwt.enums.JwtErrorCode;

public class JwtClaimsEmptyException extends CustomException {
    public JwtClaimsEmptyException() {
        super(JwtErrorCode.JWT_CLAIMS_EMPTY);
    }
}