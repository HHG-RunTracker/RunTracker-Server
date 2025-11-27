package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class CourseUpdateForbiddenException extends CustomException {
    public CourseUpdateForbiddenException() {
        super(CourseErrorCode.COURSE_UPDATE_FORBIDDEN);
    }

    public CourseUpdateForbiddenException(String message) {
        super(CourseErrorCode.COURSE_UPDATE_FORBIDDEN, message);
    }
}