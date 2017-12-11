package com.leafyjava.pannellumtourmaker.exceptions;

public class TourNotFoundException extends RuntimeException {
    public TourNotFoundException(final String message) {
        super(message);
    }

    public TourNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
