package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CannotFinishNotRunningException extends CustomException {
    public CannotFinishNotRunningException() {
        super(CrewErrorCode.CANNOT_FINISH_NOT_RUNNING);
    }

    public CannotFinishNotRunningException(String message) {
        super(CrewErrorCode.CANNOT_FINISH_NOT_RUNNING, message);
    }
}