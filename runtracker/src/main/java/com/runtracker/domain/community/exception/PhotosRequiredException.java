package com.runtracker.domain.community.exception;

import com.runtracker.domain.community.enums.CommunityErrorCode;
import com.runtracker.global.exception.CustomException;

public class PhotosRequiredException extends CustomException {
    public PhotosRequiredException() {
        super(CommunityErrorCode.PHOTOS_REQUIRED);
    }

    public PhotosRequiredException(String message) {
        super(CommunityErrorCode.PHOTOS_REQUIRED, message);
    }
}