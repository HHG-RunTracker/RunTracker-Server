package com.runtracker.domain.member.exception;

import com.runtracker.domain.member.enums.MemberErrorCode;
import com.runtracker.global.exception.CustomException;

public class MemberWithdrawalFailedException extends CustomException {

    public MemberWithdrawalFailedException() {
        super(MemberErrorCode.MEMBER_WITHDRAWAL_FAILED);
    }

    public MemberWithdrawalFailedException(String message) {
        super(MemberErrorCode.MEMBER_WITHDRAWAL_FAILED, message);
    }
}