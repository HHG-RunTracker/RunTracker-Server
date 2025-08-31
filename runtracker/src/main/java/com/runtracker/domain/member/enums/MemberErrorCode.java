package com.runtracker.domain.member.enums;

import com.runtracker.global.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ResponseCode {

    MEMBER_NOT_FOUND("M001", "Member not found"),
    MEMBER_WITHDRAWAL_FAILED("M002", "Member withdrawal failed"),
    INVALID_DIFFICULTY("M003", "Invalid difficulty value"),
    BACKUP_NOT_FOUND("M004", "Backup not found"),
    BACKUP_SERIALIZATION_FAILED("M005", "Failed to serialize backup data"),
    BACKUP_DESERIALIZATION_FAILED("M006", "Failed to deserialize backup data"),
    BACKUP_ALREADY_RESTORED("M007", "Backup has already been restored");

    private final String statusCode;
    private final String message;
}