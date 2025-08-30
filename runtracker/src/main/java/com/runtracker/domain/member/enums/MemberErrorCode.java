package com.runtracker.domain.member.enums;

import com.runtracker.global.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ResponseCode {

    MEMBER_NOT_FOUND("M001", "Member not found"),
    MEMBER_WITHDRAWAL_FAILED("M002", "Member withdrawal failed"),
    INVALID_DIFFICULTY("M003", "Invalid difficulty value");

    private final String statusCode;
    private final String message;
}