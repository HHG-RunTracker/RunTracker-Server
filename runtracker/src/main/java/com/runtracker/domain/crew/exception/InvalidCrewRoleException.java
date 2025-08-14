package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class InvalidCrewRoleException extends CustomException {
    public InvalidCrewRoleException() {
        super(CrewErrorCode.INVALID_CREW_ROLE);
    }
}