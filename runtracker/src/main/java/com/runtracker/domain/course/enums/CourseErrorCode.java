package com.runtracker.domain.course.enums;

import com.runtracker.global.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseErrorCode implements ResponseCode {
    
    COURSE_CREATION_FAILED("CR001", "Course creation failed"),
    COURSE_SAVE_FAILED("CR002", "Course save failed"),
    NEARBY_COURSE_MAPPING_FAILED("CR003", "Nearby course mapping failed"),
    COORDINATES_PARSING_FAILED("CR004", "Coordinates parsing failed"),
    COURSE_NOT_FOUND("CR005", "Course not found"),
    VALIDATION_ERROR("CR006", "validation error"),
    MULTIPLE_ACTIVE_RUNNING("CR007", "Multiple active running found"),
    ALREADY_RUNNING("CR008", "Already running"),
    INVALID_START_TIME("CR009", "Start time cannot be in the future");
    
    private final String statusCode;
    private final String message;
}