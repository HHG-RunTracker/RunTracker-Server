package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CrewRunningNotWaitingException extends CustomException {
    public CrewRunningNotWaitingException() {
        super(CrewErrorCode.CREW_RUNNING_NOT_WAITING);
    }

    public CrewRunningNotWaitingException(String message) {
        super(CrewErrorCode.CREW_RUNNING_NOT_WAITING, message);
    }
}