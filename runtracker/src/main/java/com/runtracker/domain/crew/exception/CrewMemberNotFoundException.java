package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CrewMemberNotFoundException extends CustomException {
    public CrewMemberNotFoundException() {
        super(CrewErrorCode.CREW_MEMBER_NOT_FOUND);
    }

    public CrewMemberNotFoundException(String message) {
        super(CrewErrorCode.CREW_MEMBER_NOT_FOUND, message);
    }
}