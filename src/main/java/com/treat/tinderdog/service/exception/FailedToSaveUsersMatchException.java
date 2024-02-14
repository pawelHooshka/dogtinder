package com.treat.tinderdog.service.exception;

public class FailedToSaveUsersMatchException extends RuntimeException {

    private static final String ERROR_MESSAGE =
            "Could not save a new match for the user username=%s and user username=%d";

    public FailedToSaveUsersMatchException(final String username, final Long likedUserId) {
        super(String.format(ERROR_MESSAGE, username, likedUserId));
    }
}
