package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class NoRecommendedCoursesException extends CustomException {
    public NoRecommendedCoursesException() {
        super(CourseErrorCode.NO_RECOMMENDED_COURSES);
    }

    public NoRecommendedCoursesException(String message) {
        super(CourseErrorCode.NO_RECOMMENDED_COURSES, message);
    }
}