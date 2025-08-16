package com.runtracker.domain.record.enums;

import com.runtracker.global.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecordErrorCode implements ResponseCode {
    
    COURSE_NOT_FOUND_FOR_RECORD("RC001", "Course not found for record"),
    INVALID_DATE_RANGE("RC002", "Start date must be before or equal to end date"),
    DATE_RANGE_TOO_LARGE("RC003", "Date range cannot exceed 365 days"),
    DATE_PARAMETER_REQUIRED("RC004", "Date parameters are required"),
    NO_RECORDS_FOUND("RC005", "No records found for the specified date range");
    
    private final String statusCode;
    private final String message;
}