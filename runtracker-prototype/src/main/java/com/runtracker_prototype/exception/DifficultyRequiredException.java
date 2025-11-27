package com.runtracker_prototype.exception;

import com.runtracker_prototype.errorCode.CourseErrorCode;

public class DifficultyRequiredException extends CustomException {
    public DifficultyRequiredException() {
        super(CourseErrorCode.DIFFICULTY_REQUIRED);
    }
} 