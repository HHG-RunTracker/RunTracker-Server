package com.runtracker.domain.member.exception;

import com.runtracker.domain.member.enums.MemberErrorCode;
import com.runtracker.global.exception.CustomException;

public class BackupDeserializationException extends CustomException {
    public BackupDeserializationException(String message) {
        super(MemberErrorCode.BACKUP_DESERIALIZATION_FAILED, message);
    }
}