package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class NotJoinedCrewRunningException extends CustomException {
    public NotJoinedCrewRunningException() {
        super(CrewErrorCode.NOT_JOINED_CREW_RUNNING);
    }

    public NotJoinedCrewRunningException(String message) {
        super(CrewErrorCode.NOT_JOINED_CREW_RUNNING, message);
    }
}