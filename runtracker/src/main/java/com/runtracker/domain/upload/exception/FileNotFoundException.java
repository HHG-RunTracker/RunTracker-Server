package com.runtracker.domain.upload.exception;

import com.runtracker.domain.upload.enums.UploadErrorCode;
import com.runtracker.global.exception.CustomException;

public class FileNotFoundException extends CustomException {
    public FileNotFoundException() {
        super(UploadErrorCode.FILE_NOT_FOUND);
    }

    public FileNotFoundException(String message) {
        super(UploadErrorCode.FILE_NOT_FOUND, message);
    }
}