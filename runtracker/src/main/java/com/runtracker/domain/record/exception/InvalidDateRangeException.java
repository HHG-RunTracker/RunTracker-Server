package com.runtracker.domain.record.exception;

import com.runtracker.domain.record.enums.RecordErrorCode;
import com.runtracker.global.exception.CustomException;

public class InvalidDateRangeException extends CustomException {
    public InvalidDateRangeException() {
        super(RecordErrorCode.INVALID_DATE_RANGE);
    }

    public InvalidDateRangeException(String message) {
        super(RecordErrorCode.INVALID_DATE_RANGE, message);
    }
}