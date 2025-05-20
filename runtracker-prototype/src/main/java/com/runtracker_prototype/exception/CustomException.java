package com.runtracker_prototype.exception;

import com.runtracker_prototype.code.ResponseCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ResponseCode errorCode;

    public CustomException(ResponseCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
} 