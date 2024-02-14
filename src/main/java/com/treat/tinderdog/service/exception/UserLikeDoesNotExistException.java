package com.treat.tinderdog.service.exception;

import java.util.UUID;

public class UserLikeDoesNotExistException extends RuntimeException {

    private final static String ERROR_MESSAGE = "User like username=%s, likeId=%s does not exist";

    public UserLikeDoesNotExistException(final String username, final UUID likeId) {
        super(String.format(ERROR_MESSAGE, username, likeId));
    }
}
