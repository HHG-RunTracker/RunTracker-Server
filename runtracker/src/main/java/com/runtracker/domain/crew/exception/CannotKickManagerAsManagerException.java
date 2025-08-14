package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CannotKickManagerAsManagerException extends CustomException {
    public CannotKickManagerAsManagerException() {
        super(CrewErrorCode.CANNOT_KICK_MANAGER_AS_MANAGER);
    }
}