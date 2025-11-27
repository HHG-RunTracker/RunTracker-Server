package com.runtracker.domain.member.exception;

import com.runtracker.domain.member.enums.MemberErrorCode;
import com.runtracker.global.exception.CustomException;

public class BackupAlreadyRestoredException extends CustomException {
    public BackupAlreadyRestoredException(String message) {
        super(MemberErrorCode.BACKUP_ALREADY_RESTORED, message);
    }
}