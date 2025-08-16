package com.runtracker.domain.record.exception;

import com.runtracker.domain.record.enums.RecordErrorCode;
import com.runtracker.global.exception.CustomException;

public class RecordNotFoundException extends CustomException {
    
    public RecordNotFoundException(String message) {
        super(RecordErrorCode.RECORD_NOT_FOUND, message);
    }
}