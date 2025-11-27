package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class CrewSearchResultNotFoundException extends CustomException {
    public CrewSearchResultNotFoundException() {
        super(CrewErrorCode.CREW_SEARCH_RESULT_NOT_FOUND);
    }
}