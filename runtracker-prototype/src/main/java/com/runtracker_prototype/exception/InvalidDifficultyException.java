package com.runtracker_prototype.exception;

import com.runtracker_prototype.errorCode.CourseErrorCode;

public class InvalidDifficultyException extends CustomException {
    public InvalidDifficultyException() {
        super(CourseErrorCode.INVALID_DIFFICULTY);
    }
} 