package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class InvalidStartTimeException extends CustomException {
    public InvalidStartTimeException() {
        super(CourseErrorCode.INVALID_START_TIME);
    }

    public InvalidStartTimeException(String message) {
        super(CourseErrorCode.INVALID_START_TIME, message);
    }
}