package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CannotModifyLeaderRoleException extends CustomException {
    public CannotModifyLeaderRoleException() {
        super(CrewErrorCode.CANNOT_MODIFY_LEADER_ROLE);
    }
}