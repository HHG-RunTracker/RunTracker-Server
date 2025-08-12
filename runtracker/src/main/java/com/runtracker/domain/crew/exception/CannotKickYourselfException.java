package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CannotKickYourselfException extends CustomException {
    public CannotKickYourselfException() {
        super(CrewErrorCode.CANNOT_KICK_YOURSELF);
    }
}