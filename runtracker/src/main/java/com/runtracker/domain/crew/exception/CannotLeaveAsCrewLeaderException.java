package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CannotLeaveAsCrewLeaderException extends CustomException {
    public CannotLeaveAsCrewLeaderException() {
        super(CrewErrorCode.CANNOT_LEAVE_AS_CREW_LEADER);
    }

    public CannotLeaveAsCrewLeaderException(String message) {
        super(CrewErrorCode.CANNOT_LEAVE_AS_CREW_LEADER, message);
    }
}