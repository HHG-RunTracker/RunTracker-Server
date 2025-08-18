package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CannotDeleteStartedRunningException extends CustomException {
    public CannotDeleteStartedRunningException() {
        super(CrewErrorCode.CANNOT_DELETE_STARTED_RUNNING);
    }

    public CannotDeleteStartedRunningException(String message) {
        super(CrewErrorCode.CANNOT_DELETE_STARTED_RUNNING, message);
    }
}