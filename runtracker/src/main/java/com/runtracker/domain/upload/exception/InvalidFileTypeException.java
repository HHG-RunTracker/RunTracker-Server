package com.runtracker.domain.upload.exception;

import com.runtracker.domain.upload.enums.UploadErrorCode;
import com.runtracker.global.exception.CustomException;

public class InvalidFileTypeException extends CustomException {
    public InvalidFileTypeException() {
        super(UploadErrorCode.INVALID_FILE_TYPE);
    }

    public InvalidFileTypeException(String message) {
        super(UploadErrorCode.INVALID_FILE_TYPE, message);
    }
}