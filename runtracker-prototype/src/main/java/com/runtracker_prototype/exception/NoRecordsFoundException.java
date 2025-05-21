package com.runtracker_prototype.exception;

import com.runtracker_prototype.errorCode.CourseErrorCode;

public class NoRecordsFoundException extends CustomException {
    public NoRecordsFoundException() {
        super(CourseErrorCode.NO_RECORDS_FOUND);
    }
} 