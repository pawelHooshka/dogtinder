package com.treat.tinderdog.service.exception;

public class UserNotFoundException extends RuntimeException {

    private final static String ERROR_MESSAGE = "User username=%s does not exist";

    public UserNotFoundException(final String username) {
        super(String.format(ERROR_MESSAGE, username));
    }
}
