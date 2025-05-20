package com.runtracker_prototype.errorCode;

import com.runtracker_prototype.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseErrorCode implements ResponseCode {

    INVALID_COURSE_DATA("CS001", "유효하지 않은 코스 데이터입니다"),
    COURSE_CREATION_FAILED("CS002", "코스 생성에 실패했습니다"),
    COURSE_NOT_FOUND("CS003", "존재하지 않는 코스입니다"),
    INVALID_COORDINATE_DATA("CS004", "유효하지 않은 좌표 데이터입니다");

    private final String statusCode;
    private final String message;
} 