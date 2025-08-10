package com.runtracker.domain.record.exception;

import com.runtracker.domain.record.enums.RecordErrorCode;
import com.runtracker.global.exception.CustomException;

public class NoRecordsFoundException extends CustomException {
    public NoRecordsFoundException() {
        super(RecordErrorCode.NO_RECORDS_FOUND);
    }

    public NoRecordsFoundException(String message) {
        super(RecordErrorCode.NO_RECORDS_FOUND, message);
    }
}