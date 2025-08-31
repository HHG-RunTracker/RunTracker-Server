package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CrewRunningNotFoundException extends CustomException {
    public CrewRunningNotFoundException() {
        super(CrewErrorCode.CREW_RUNNING_NOT_FOUND);
    }

    public CrewRunningNotFoundException(String message) {
        super(CrewErrorCode.CREW_RUNNING_NOT_FOUND, message);
    }
}