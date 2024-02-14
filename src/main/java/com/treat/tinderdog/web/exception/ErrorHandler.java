package com.treat.tinderdog.web.exception;

import com.treat.tinderdog.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            UserNotFoundException.class,
            UserLikeDoesNotExistException.class,
            UserMatchDoesNotExistException.class,
            ProfileNotFoundException.class
    })
    protected ResponseEntity<Error> handleNotFoundExceptions(final RuntimeException exception) {
        return new ResponseEntity<>(
                new Error(exception.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({
            MatchAlreadyExistsException.class,
            LikeAlreadyExistsException.class,
            UserIdAndLikedProfileIdViolationException.class
    })
    protected ResponseEntity<Error> handleConstraintViolation(final RuntimeException exception) {
        return new ResponseEntity<>(
                new Error(exception.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(PagingOrSortingPropertiesInvalidException.class)
    protected ResponseEntity<Error> handleMissingParams(final PagingOrSortingPropertiesInvalidException exception) {
        return new ResponseEntity<>(
                new Error(exception.getMessage()),
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    @ExceptionHandler(NotAuthorisedToAccessResourceException.class)
    protected ResponseEntity<Error> handleAccessRightsViolation(final NotAuthorisedToAccessResourceException exception) {
        return new ResponseEntity<>(
                new Error(exception.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }
}
