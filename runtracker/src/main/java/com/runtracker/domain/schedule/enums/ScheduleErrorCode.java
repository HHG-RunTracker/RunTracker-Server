package com.runtracker.domain.schedule.enums;

import com.runtracker.global.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ScheduleErrorCode implements ResponseCode {

    SCHEDULE_NOT_FOUND("SH001", "Schedule not found"),
    SCHEDULE_CREATION_FAILED("SH002", "Schedule creation failed"),
    INVALID_SCHEDULE_DATE("SH003", "Invalid schedule date"),
    UNAUTHORIZED_SCHEDULE_ACCESS("SH004", "Unauthorized schedule access");

    private final String statusCode;
    private final String message;
}