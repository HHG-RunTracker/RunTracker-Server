package com.runtracker.domain.schedule.enums;

import com.runtracker.global.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ScheduleErrorCode implements ResponseCode {

    SCHEDULE_NOT_FOUND("SH001", "일정을 찾을 수 없습니다."),
    SCHEDULE_CREATION_FAILED("SH002", "일정 등록에 실패했습니다."),
    INVALID_SCHEDULE_DATE("SH003", "유효하지 않은 일정 날짜입니다."),
    UNAUTHORIZED_SCHEDULE_ACCESS("SH004", "일정에 대한 접근 권한이 없습니다.");

    private final String statusCode;
    private final String message;
}