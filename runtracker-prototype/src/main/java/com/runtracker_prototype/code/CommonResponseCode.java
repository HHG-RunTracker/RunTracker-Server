package com.runtracker_prototype.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonResponseCode implements ResponseCode {

    OK("C000", "success"),
    BAD_REQUEST_ERROR("C001", "api bad request exception"),
    REQUEST_BODY_MISSING_ERROR("C002", "required request body is missing"),
    MISSING_REQUEST_PARAMETER_ERROR("C003", "missing servlet requestParameter exception"),
    IO_ERROR("C004", "I/O exception"),
    JSON_PARSE_ERROR("C005", "json parse exception"),
    JACKSON_PROCESS_ERROR("C006", "com.fasterxml.jackson.core exception"),
    FORBIDDEN_ERROR("C007", "forbidden exception"),
    NOT_FOUND_ERROR("C008", "not found exception"),
    NULL_POINT_ERROR("C009", "null point exception"),
    NOT_VALID_ERROR("C010", "handle validation exception"),
    NOT_VALID_HEADER_ERROR("C011", "not valid header exception"),
    NOT_VALID_TIME_ERROR("C012", "Requests are not allowed before 19:00 on Mondays"),
    INTERNAL_SERVER_ERROR("C999", "internal server error exception");

    private final String statusCode;
    private final String message;
} 