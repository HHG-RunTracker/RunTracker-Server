package com.runtracker.domain.schedule.exception;

import com.runtracker.domain.schedule.enums.ScheduleErrorCode;
import com.runtracker.global.exception.CustomException;

public class UnauthorizedScheduleAccessException extends CustomException {
    public UnauthorizedScheduleAccessException() {
        super(ScheduleErrorCode.UNAUTHORIZED_SCHEDULE_ACCESS);
    }

    public UnauthorizedScheduleAccessException(String message) {
        super(ScheduleErrorCode.UNAUTHORIZED_SCHEDULE_ACCESS, message);
    }
}