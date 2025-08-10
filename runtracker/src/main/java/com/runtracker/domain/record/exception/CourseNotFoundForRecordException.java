package com.runtracker.domain.record.exception;

import com.runtracker.domain.record.enums.RecordErrorCode;
import com.runtracker.global.exception.CustomException;

public class CourseNotFoundForRecordException extends CustomException {
    public CourseNotFoundForRecordException() {
        super(RecordErrorCode.COURSE_NOT_FOUND_FOR_RECORD);
    }

    public CourseNotFoundForRecordException(String message) {
        super(RecordErrorCode.COURSE_NOT_FOUND_FOR_RECORD, message);
    }
}