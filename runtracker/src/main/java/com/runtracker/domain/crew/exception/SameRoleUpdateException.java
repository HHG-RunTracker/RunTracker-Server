package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class SameRoleUpdateException extends CustomException {
    public SameRoleUpdateException() {
        super(CrewErrorCode.SAME_ROLE_UPDATE);
    }
}