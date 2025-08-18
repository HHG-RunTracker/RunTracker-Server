package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CrewRunningCreationFailedException extends CustomException {
    public CrewRunningCreationFailedException() {
        super(CrewErrorCode.CREW_RUNNING_CREATION_FAILED);
    }

    public CrewRunningCreationFailedException(String message) {
        super(CrewErrorCode.CREW_RUNNING_CREATION_FAILED, message);
    }
}