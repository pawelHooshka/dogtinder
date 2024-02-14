package com.treat.tinderdog.service.exception;

public class FailedToSaveUsersLikeException extends RuntimeException {

    private static final String ERROR_MESSAGE = "Could not save a new like for the user username=%s";

    public FailedToSaveUsersLikeException(final String username) {
        super(String.format(ERROR_MESSAGE, username));
    }
}
