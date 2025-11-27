package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class NotCrewLeaderException extends CustomException {
    public NotCrewLeaderException() {
        super(CrewErrorCode.NOT_CREW_LEADER);
    }

    public NotCrewLeaderException(String message) {
        super(CrewErrorCode.NOT_CREW_LEADER, message);
    }
}