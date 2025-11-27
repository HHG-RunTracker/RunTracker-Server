package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class NoPathDataException extends CustomException {
    public NoPathDataException() {
        super(CourseErrorCode.NO_PATH_DATA);
    }
}