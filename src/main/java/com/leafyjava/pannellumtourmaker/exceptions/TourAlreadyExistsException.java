package com.leafyjava.pannellumtourmaker.exceptions;

public class TourAlreadyExistsException extends RuntimeException {
    public TourAlreadyExistsException(final String message) {
        super(message);
    }

    public TourAlreadyExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
