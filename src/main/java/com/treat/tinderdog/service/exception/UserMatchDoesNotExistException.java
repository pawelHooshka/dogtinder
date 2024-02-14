package com.treat.tinderdog.service.exception;

public class UserMatchDoesNotExistException extends RuntimeException {

    private final static String ERROR_MESSAGE = "User match username=%s, matchedProfileId=%d does not exist";

    public UserMatchDoesNotExistException(final String username, final Long matchedProfileId) {
        super(String.format(ERROR_MESSAGE, username, matchedProfileId));
    }
}
