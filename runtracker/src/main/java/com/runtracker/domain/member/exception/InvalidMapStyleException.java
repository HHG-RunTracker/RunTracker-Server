package com.runtracker.domain.member.exception;

import com.runtracker.domain.member.enums.MemberErrorCode;
import com.runtracker.global.exception.CustomException;

public class InvalidMapStyleException extends CustomException {

    public InvalidMapStyleException() {
        super(MemberErrorCode.INVALID_MAP_STYLE);
    }

    public InvalidMapStyleException(String message) {
        super(MemberErrorCode.INVALID_MAP_STYLE, message);
    }
}