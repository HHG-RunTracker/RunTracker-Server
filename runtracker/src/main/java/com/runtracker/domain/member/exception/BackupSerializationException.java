package com.runtracker.domain.member.exception;

import com.runtracker.domain.member.enums.MemberErrorCode;
import com.runtracker.global.exception.CustomException;

public class BackupSerializationException extends CustomException {
    public BackupSerializationException(String message) {
        super(MemberErrorCode.BACKUP_SERIALIZATION_FAILED, message);
    }
}