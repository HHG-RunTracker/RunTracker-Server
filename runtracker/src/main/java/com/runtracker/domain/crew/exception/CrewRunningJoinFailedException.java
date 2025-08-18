package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CrewRunningJoinFailedException extends CustomException {
    public CrewRunningJoinFailedException() {
        super(CrewErrorCode.CREW_RUNNING_JOIN_FAILED);
    }

    public CrewRunningJoinFailedException(String message) {
        super(CrewErrorCode.CREW_RUNNING_JOIN_FAILED, message);
    }
}