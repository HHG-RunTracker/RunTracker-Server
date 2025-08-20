package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class AlreadyInActiveCrewRunningException extends CustomException {
    public AlreadyInActiveCrewRunningException() {
        super(CrewErrorCode.ALREADY_IN_ACTIVE_CREW_RUNNING);
    }

    public AlreadyInActiveCrewRunningException(String message) {
        super(CrewErrorCode.ALREADY_IN_ACTIVE_CREW_RUNNING, message);
    }
}