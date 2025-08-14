package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CrewApplicationPendingException extends CustomException {
    public CrewApplicationPendingException() {
        super(CrewErrorCode.CREW_APPLICATION_PENDING);
    }

    public CrewApplicationPendingException(String message) {
        super(CrewErrorCode.CREW_APPLICATION_PENDING, message);
    }
}