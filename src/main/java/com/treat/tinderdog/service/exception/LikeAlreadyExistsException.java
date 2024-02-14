package com.treat.tinderdog.service.exception;

public class LikeAlreadyExistsException extends RuntimeException {

    private final static String ERROR_MESSAGE = "Like already exists user username=%s and likedProfileId=%d";

    public LikeAlreadyExistsException(final String username, final Long profileId) {
        super(String.format(ERROR_MESSAGE, username, profileId));
    }
}
