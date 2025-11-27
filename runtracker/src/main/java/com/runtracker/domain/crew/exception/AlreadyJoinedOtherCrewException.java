package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class AlreadyJoinedOtherCrewException extends CustomException {
    public AlreadyJoinedOtherCrewException() {
        super(CrewErrorCode.ALREADY_JOINED_OTHER_CREW);
    }
}