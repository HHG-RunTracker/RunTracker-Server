package com.runtracker.domain.schedule.exception;

import com.runtracker.domain.schedule.enums.ScheduleErrorCode;
import com.runtracker.global.exception.CustomException;

public class ScheduleNotFoundException extends CustomException {
    public ScheduleNotFoundException() {
        super(ScheduleErrorCode.SCHEDULE_NOT_FOUND);
    }
}