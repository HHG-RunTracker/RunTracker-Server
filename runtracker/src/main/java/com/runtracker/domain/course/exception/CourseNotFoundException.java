package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class CourseNotFoundException extends CustomException {
    public CourseNotFoundException() {
        super(CourseErrorCode.COURSE_NOT_FOUND);
    }

    public CourseNotFoundException(String message) {
        super(CourseErrorCode.COURSE_NOT_FOUND, message);
    }
}