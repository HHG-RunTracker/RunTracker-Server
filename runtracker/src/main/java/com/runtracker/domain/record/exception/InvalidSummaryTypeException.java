package com.runtracker.domain.record.exception;

import com.runtracker.domain.record.enums.RecordErrorCode;
import com.runtracker.global.exception.CustomException;

public class InvalidSummaryTypeException extends CustomException {
    public InvalidSummaryTypeException(String message) {
        super(RecordErrorCode.INVALID_SUMMARY_TYPE, message);
    }
}