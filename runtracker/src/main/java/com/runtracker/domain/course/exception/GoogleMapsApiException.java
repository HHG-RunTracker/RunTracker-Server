package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class GoogleMapsApiException extends CustomException {
    public GoogleMapsApiException() {
        super(CourseErrorCode.GOOGLE_MAPS_API_ERROR);
    }
}