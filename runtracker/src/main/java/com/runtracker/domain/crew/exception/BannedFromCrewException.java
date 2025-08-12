package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class BannedFromCrewException extends CustomException {
    public BannedFromCrewException() {
        super(CrewErrorCode.BANNED_FROM_CREW);
    }
}