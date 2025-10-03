package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class RecommendationServiceUnavailableException extends CustomException {
    public RecommendationServiceUnavailableException() {
        super(CourseErrorCode.RECOMMENDATION_SERVICE_UNAVAILABLE);
    }

    public RecommendationServiceUnavailableException(String message) {
        super(CourseErrorCode.RECOMMENDATION_SERVICE_UNAVAILABLE, message);
    }
}