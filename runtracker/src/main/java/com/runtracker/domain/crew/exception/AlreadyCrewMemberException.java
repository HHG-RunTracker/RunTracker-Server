package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class AlreadyCrewMemberException extends CustomException {
    public AlreadyCrewMemberException() {
        super(CrewErrorCode.ALREADY_CREW_MEMBER);
    }

    public AlreadyCrewMemberException(String message) {
        super(CrewErrorCode.ALREADY_CREW_MEMBER, message);
    }
}