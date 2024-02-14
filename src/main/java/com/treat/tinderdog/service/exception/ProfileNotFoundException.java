package com.treat.tinderdog.service.exception;

public class ProfileNotFoundException extends RuntimeException {

    private static final String ERROR_MESSAGE = "Profile id=%d not found";

    public ProfileNotFoundException(final long profileId) {
        super(String.format(ERROR_MESSAGE, profileId));
    }
}
