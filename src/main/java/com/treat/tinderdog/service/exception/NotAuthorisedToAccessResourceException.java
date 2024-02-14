package com.treat.tinderdog.service.exception;

import com.treat.tinderdog.data.CustomUserDetails;

import java.util.UUID;

public class NotAuthorisedToAccessResourceException extends RuntimeException {

    private static final String ERROR_MESSAGE =
            "User, userId=%d, username=%s is not authorised to access resource '%s' with id=%s";

    public NotAuthorisedToAccessResourceException(final CustomUserDetails userDetails,
                                                  final String resourceType,
                                                  final UUID resourceId) {
        super(String.format(ERROR_MESSAGE, userDetails.getId(), userDetails.getUsername(), resourceType, resourceId));
    }
}
