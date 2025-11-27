package com.runtracker.domain.upload.exception;

import com.runtracker.domain.upload.enums.UploadErrorCode;
import com.runtracker.global.exception.CustomException;

public class FileStorageFailedException extends CustomException {
    public FileStorageFailedException() {
        super(UploadErrorCode.FILE_STORAGE_FAILED);
    }

    public FileStorageFailedException(String message) {
        super(UploadErrorCode.FILE_STORAGE_FAILED, message);
    }
}