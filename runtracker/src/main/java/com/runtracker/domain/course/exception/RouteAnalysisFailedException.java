package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class RouteAnalysisFailedException extends CustomException {
    public RouteAnalysisFailedException() {
        super(CourseErrorCode.ROUTE_ANALYSIS_FAILED);
    }
}