package com.runtracker.domain.upload.exception;

import com.runtracker.domain.upload.enums.UploadErrorCode;
import com.runtracker.global.exception.CustomException;

public class FileIsEmptyException extends CustomException {
    public FileIsEmptyException() {
        super(UploadErrorCode.FILE_IS_EMPTY);
    }

    public FileIsEmptyException(String message) {
        super(UploadErrorCode.FILE_IS_EMPTY, message);
    }
}