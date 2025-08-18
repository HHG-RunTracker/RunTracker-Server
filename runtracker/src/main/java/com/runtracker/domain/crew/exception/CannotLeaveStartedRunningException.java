package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CannotLeaveStartedRunningException extends CustomException {
    public CannotLeaveStartedRunningException() {
        super(CrewErrorCode.CANNOT_LEAVE_STARTED_RUNNING);
    }

    public CannotLeaveStartedRunningException(String message) {
        super(CrewErrorCode.CANNOT_LEAVE_STARTED_RUNNING, message);
    }
}