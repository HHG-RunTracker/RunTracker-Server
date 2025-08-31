package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class AlreadyJoinedCrewRunningException extends CustomException {
    public AlreadyJoinedCrewRunningException() {
        super(CrewErrorCode.ALREADY_JOINED_CREW_RUNNING);
    }

    public AlreadyJoinedCrewRunningException(String message) {
        super(CrewErrorCode.ALREADY_JOINED_CREW_RUNNING, message);
    }
}