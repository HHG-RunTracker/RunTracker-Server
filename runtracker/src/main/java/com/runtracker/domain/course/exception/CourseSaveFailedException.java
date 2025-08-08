package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class CourseSaveFailedException extends CustomException {

    public CourseSaveFailedException() {
        super(CourseErrorCode.COURSE_SAVE_FAILED);
    }

    public CourseSaveFailedException(String message) {
        super(CourseErrorCode.COURSE_SAVE_FAILED, message);
    }
}