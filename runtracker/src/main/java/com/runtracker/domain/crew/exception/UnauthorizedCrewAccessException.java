package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class UnauthorizedCrewAccessException extends CustomException {
    
    public UnauthorizedCrewAccessException() {
        super(CrewErrorCode.UNAUTHORIZED_CREW_ACCESS);
    }
}