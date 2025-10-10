package com.runtracker.domain.upload.exception;

import com.runtracker.domain.upload.enums.UploadErrorCode;
import com.runtracker.global.exception.CustomException;

public class InvalidFileNameException extends CustomException {
    public InvalidFileNameException() {
        super(UploadErrorCode.INVALID_FILE_NAME);
    }

    public InvalidFileNameException(String message) {
        super(UploadErrorCode.INVALID_FILE_NAME, message);
    }
}