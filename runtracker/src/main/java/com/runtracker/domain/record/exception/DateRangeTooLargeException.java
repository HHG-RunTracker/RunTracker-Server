package com.runtracker.domain.record.exception;

import com.runtracker.domain.record.enums.RecordErrorCode;
import com.runtracker.global.exception.CustomException;

public class DateRangeTooLargeException extends CustomException {
    public DateRangeTooLargeException() {
        super(RecordErrorCode.DATE_RANGE_TOO_LARGE);
    }

    public DateRangeTooLargeException(String message) {
        super(RecordErrorCode.DATE_RANGE_TOO_LARGE, message);
    }
}