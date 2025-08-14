package com.runtracker.domain.member.exception;

import com.runtracker.domain.member.enums.MemberErrorCode;
import com.runtracker.global.exception.CustomException;

public class MemberNotFoundException extends CustomException {

    public MemberNotFoundException() {
        super(MemberErrorCode.MEMBER_NOT_FOUND);
    }

    public MemberNotFoundException(String message) {
        super(MemberErrorCode.MEMBER_NOT_FOUND, message);
    }
}