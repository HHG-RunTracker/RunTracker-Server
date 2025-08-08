package com.runtracker.domain.course.exception;

import com.runtracker.domain.course.entity.enums.CourseErrorCode;
import com.runtracker.global.exception.CustomException;

public class NearbyCourseMappingFailedException extends CustomException {

    public NearbyCourseMappingFailedException() {
        super(CourseErrorCode.NEARBY_COURSE_MAPPING_FAILED);
    }

    public NearbyCourseMappingFailedException(String message) {
        super(CourseErrorCode.NEARBY_COURSE_MAPPING_FAILED, message);
    }
}