package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CrewNotFoundException extends CustomException {
    public CrewNotFoundException() {
        super(CrewErrorCode.CREW_NOT_FOUND);
    }

    public CrewNotFoundException(String message) {
        super(CrewErrorCode.CREW_NOT_FOUND, message);
    }
}