package com.runtracker.domain.record.exception;

import com.runtracker.domain.record.enums.RecordErrorCode;
import com.runtracker.global.exception.CustomException;

public class UnauthorizedRecordAccessException extends CustomException {
    
    public UnauthorizedRecordAccessException(String message) {
        super(RecordErrorCode.UNAUTHORIZED_RECORD_ACCESS, message);
    }
}