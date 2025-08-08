package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.entity.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class CourseCreationFailedException extends CustomException {

    public CourseCreationFailedException() {
        super(CourseErrorCode.COURSE_CREATION_FAILED);
    }

    public CourseCreationFailedException(String message) {
        super(CourseErrorCode.COURSE_CREATION_FAILED, message);
    }
}