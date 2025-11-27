package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class MultipleActiveRunningException extends CustomException {
    
    public MultipleActiveRunningException(String message) {
        super(CourseErrorCode.MULTIPLE_ACTIVE_RUNNING, message);
    }
}