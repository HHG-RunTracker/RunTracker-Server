package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class AlreadyRunningException extends CustomException {
    
    public AlreadyRunningException(String message) {
        super(CourseErrorCode.ALREADY_RUNNING, message);
    }
}