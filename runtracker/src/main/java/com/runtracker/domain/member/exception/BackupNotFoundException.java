package com.runtracker.domain.member.exception;

import com.runtracker.domain.member.enums.MemberErrorCode;
import com.runtracker.global.exception.CustomException;

public class BackupNotFoundException extends CustomException {
    public BackupNotFoundException(String message) {
        super(MemberErrorCode.BACKUP_NOT_FOUND, message);
    }
}