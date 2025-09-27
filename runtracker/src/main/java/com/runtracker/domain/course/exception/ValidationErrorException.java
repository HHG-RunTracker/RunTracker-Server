package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class ValidationErrorException extends CustomException {
    public ValidationErrorException(String message) {
        super(CourseErrorCode.VALIDATION_ERROR, message);
    }
}