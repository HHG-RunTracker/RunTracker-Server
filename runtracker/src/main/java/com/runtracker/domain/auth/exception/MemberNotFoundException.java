package com.runtracker.domain.auth.exception;

import com.runtracker.domain.auth.enums.AuthErrorCode;
import com.runtracker.global.exception.CustomException;

public class MemberNotFoundException extends CustomException {

    public MemberNotFoundException() {
        super(AuthErrorCode.MEMBER_NOT_FOUND);
    }

    public MemberNotFoundException(String message) {
        super(AuthErrorCode.MEMBER_NOT_FOUND, message);
    }
}