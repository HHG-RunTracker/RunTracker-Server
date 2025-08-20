package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class AlreadyFinishedCrewRunningException extends CustomException {
    public AlreadyFinishedCrewRunningException() {
        super(CrewErrorCode.ALREADY_FINISHED_CREW_RUNNING);
    }

    public AlreadyFinishedCrewRunningException(String message) {
        super(CrewErrorCode.ALREADY_FINISHED_CREW_RUNNING, message);
    }
}