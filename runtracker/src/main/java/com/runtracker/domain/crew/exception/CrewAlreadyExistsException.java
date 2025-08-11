package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CrewAlreadyExistsException extends CustomException {
    public CrewAlreadyExistsException() {
        super(CrewErrorCode.CREW_ALREADY_EXISTS);
    }

    public CrewAlreadyExistsException(String message) {
        super(CrewErrorCode.CREW_ALREADY_EXISTS, message);
    }
}