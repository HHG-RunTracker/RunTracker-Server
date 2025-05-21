package com.runtracker_prototype.exception;

import com.runtracker_prototype.errorCode.CourseErrorCode;

public class CourseCreationFailedException extends CustomException {
    public CourseCreationFailedException() {
        super(CourseErrorCode.COURSE_CREATION_FAILED);
    }
} 