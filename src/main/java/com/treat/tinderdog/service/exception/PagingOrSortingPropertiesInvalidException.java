package com.treat.tinderdog.service.exception;

public class PagingOrSortingPropertiesInvalidException extends RuntimeException {

    private static final String ERROR_MESSAGE = "Provided paging or sorting parameters are in valid: " +
            "username=%s, page=%d, limit=%d, sortProperty=%s, sortDirection=%s";

    public PagingOrSortingPropertiesInvalidException(final String username,
                                                     final int page,
                                                     final int limit,
                                                     final String sortProperty,
                                                     final String sortDirection) {
        super(String.format(ERROR_MESSAGE, username, page, limit, sortProperty, sortDirection));
    }
}
