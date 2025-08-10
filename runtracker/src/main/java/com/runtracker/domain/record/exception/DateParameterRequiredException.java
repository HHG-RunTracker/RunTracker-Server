package com.runtracker.domain.record.exception;

import com.runtracker.domain.record.enums.RecordErrorCode;
import com.runtracker.global.exception.CustomException;

public class DateParameterRequiredException extends CustomException {
    public DateParameterRequiredException() {
        super(RecordErrorCode.DATE_PARAMETER_REQUIRED);
    }

    public DateParameterRequiredException(String message) {
        super(RecordErrorCode.DATE_PARAMETER_REQUIRED, message);
    }
}