package com.treat.tinderdog.service.exception;

public class MatchAlreadyExistsException extends RuntimeException {

    private final static String ERROR_MESSAGE = "Match already exists user username=%s and likedProfileId=%d";

    public MatchAlreadyExistsException(final String username, final Long profileId) {
        super(String.format(ERROR_MESSAGE, username, profileId));
    }
}
