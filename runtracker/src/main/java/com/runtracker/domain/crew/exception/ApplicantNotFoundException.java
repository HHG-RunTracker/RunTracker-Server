package com.runtracker.domain.crew.exception;

import com.runtracker.domain.crew.enums.CrewErrorCode;
import com.runtracker.global.exception.CustomException;

public class ApplicantNotFoundException extends CustomException {
    public ApplicantNotFoundException() {
        super(CrewErrorCode.APPLICANT_NOT_FOUND);
    }

    public ApplicantNotFoundException(String message) {
        super(CrewErrorCode.APPLICANT_NOT_FOUND, message);
    }
}