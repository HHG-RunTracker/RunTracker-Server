package com.runtracker_prototype.errorCode;

import com.runtracker_prototype.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecordErrorCode implements ResponseCode {
    
    NO_RECORDS_FOUND("RC001", "기록이 존재하지 않습니다"),
    RECORD_COURSE_ID_REQUIRED("RC002", "코스 ID는 필수 값입니다"),
    RECORD_TIME_REQUIRED("RC003", "러닝 시간은 필수 값입니다"),
    RECORD_KCAL_REQUIRED("RC004", "소모 칼로리는 필수 값입니다"),
    RECORD_WALK_COUNT_REQUIRED("RC005", "걸음 수는 필수 값입니다"),
    INVALID_DATETIME_FORMAT("RC006", "잘못된 날짜/시간 형식입니다"),
    COURSE_NOT_FOUND_FOR_RECORD("RC007", "기록을 저장하려는 코스가 존재하지 않습니다");

    private final String statusCode;
    private final String message;
}
