package com.treat.tinderdog.service.exception;

public class UserIdAndLikedProfileIdViolationException extends RuntimeException {

    private final static String ERROR_MESSAGE =
            "User id and likedProfileId must be different - you cannot like yourself" +
                    " user username=%s, userId=%d and likedProfileId=%d";

    public UserIdAndLikedProfileIdViolationException(final String username,
                                                     final Long userId,
                                                     final Long profileId) {
        super(String.format(ERROR_MESSAGE, username, userId, profileId));
    }
}
