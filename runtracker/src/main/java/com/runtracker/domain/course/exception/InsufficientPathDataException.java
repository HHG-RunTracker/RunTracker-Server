package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class InsufficientPathDataException extends CustomException {
    public InsufficientPathDataException() {
        super(CourseErrorCode.INSUFFICIENT_PATH_DATA);
    }
}