package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CannotKickCrewLeaderException extends CustomException {
    public CannotKickCrewLeaderException() {
        super(CrewErrorCode.CANNOT_KICK_CREW_LEADER);
    }
}