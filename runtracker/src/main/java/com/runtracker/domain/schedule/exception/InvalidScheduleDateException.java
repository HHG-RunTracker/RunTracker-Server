package com.runtracker.domain.schedule.exception;

import com.runtracker.domain.schedule.enums.ScheduleErrorCode;
import com.runtracker.global.exception.CustomException;

public class InvalidScheduleDateException extends CustomException {
    public InvalidScheduleDateException() {
        super(ScheduleErrorCode.INVALID_SCHEDULE_DATE);
    }

    public InvalidScheduleDateException(String message) {
        super(ScheduleErrorCode.INVALID_SCHEDULE_DATE, message);
    }
}