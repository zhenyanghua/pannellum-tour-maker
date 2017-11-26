package com.leafyjava.pannellumtourmaker.exceptions;

public class InvalidTourException extends RuntimeException {
    public InvalidTourException(final String message) {
        super(message);
    }

    public InvalidTourException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
