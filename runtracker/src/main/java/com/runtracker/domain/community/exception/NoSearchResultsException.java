package com.runtracker.domain.community.exception;

import com.runtracker.domain.community.enums.CommunityErrorCode;
import com.runtracker.global.exception.CustomException;

public class NoSearchResultsException extends CustomException {
    public NoSearchResultsException() {
        super(CommunityErrorCode.NO_SEARCH_RESULTS);
    }

    public NoSearchResultsException(String message) {
        super(CommunityErrorCode.NO_SEARCH_RESULTS, message);
    }
}