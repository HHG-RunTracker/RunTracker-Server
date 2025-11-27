package com.runtracker.domain.member.exception;

import com.runtracker.domain.member.enums.MemberErrorCode;
import com.runtracker.global.exception.CustomException;

public class InvalidDifficultyException extends CustomException {

    public InvalidDifficultyException() {
        super(MemberErrorCode.INVALID_DIFFICULTY);
    }

    public InvalidDifficultyException(String message) {
        super(MemberErrorCode.INVALID_DIFFICULTY, message);
    }
}