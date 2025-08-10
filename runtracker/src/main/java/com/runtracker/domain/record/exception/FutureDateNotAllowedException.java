package com.runtracker.domain.record.exception;

import com.runtracker.domain.record.enums.RecordErrorCode;
import com.runtracker.global.exception.CustomException;

public class FutureDateNotAllowedException extends CustomException {
    public FutureDateNotAllowedException() {
        super(RecordErrorCode.FUTURE_DATE_NOT_ALLOWED);
    }

    public FutureDateNotAllowedException(String message) {
        super(RecordErrorCode.FUTURE_DATE_NOT_ALLOWED, message);
    }
}