package com.runtracker.domain.auth.enums;

import com.runtracker.global.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ResponseCode {

    INVALID_REFRESH_TOKEN("AT001", "Invalid refresh token"),
    TOKEN_REFRESH_FAILED("AT002", "Token refresh failed"),
    MEMBER_NOT_FOUND("AT003", "Member not found");

    private final String statusCode;

    private final String message;
}
