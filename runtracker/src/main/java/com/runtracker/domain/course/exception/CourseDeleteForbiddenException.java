package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class CourseDeleteForbiddenException extends CustomException {
    public CourseDeleteForbiddenException() {
        super(CourseErrorCode.COURSE_DELETE_FORBIDDEN);
    }

    public CourseDeleteForbiddenException(String message) {
        super(CourseErrorCode.COURSE_DELETE_FORBIDDEN, message);
    }
}