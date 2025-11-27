package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class NoPendingApplicationException extends CustomException {
    public NoPendingApplicationException() {
        super(CrewErrorCode.NO_PENDING_APPLICATION);
    }

    public NoPendingApplicationException(String message) {
        super(CrewErrorCode.NO_PENDING_APPLICATION, message);
    }
}