package com.runtracker.domain.schedule.exception;

import com.runtracker.domain.schedule.enums.ScheduleErrorCode;
import com.runtracker.global.exception.CustomException;

public class ScheduleCreationFailedException extends CustomException {
    public ScheduleCreationFailedException() {
        super(ScheduleErrorCode.SCHEDULE_CREATION_FAILED);
    }
}