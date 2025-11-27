package com.runtracker_prototype.exception;

import com.runtracker_prototype.errorCode.RecordErrorCode;

public class NoRecordsFoundException extends CustomException {
    public NoRecordsFoundException() {
        super(RecordErrorCode.NO_RECORDS_FOUND);
    }
} 