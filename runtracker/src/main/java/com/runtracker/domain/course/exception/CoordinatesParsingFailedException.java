package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class CoordinatesParsingFailedException extends CustomException {

    public CoordinatesParsingFailedException() {
        super(CourseErrorCode.COORDINATES_PARSING_FAILED);
    }

    public CoordinatesParsingFailedException(String message) {
        super(CourseErrorCode.COORDINATES_PARSING_FAILED, message);
    }
}