package com.leafyjava.pannellumtourmaker.exceptions;

public class ExternalCommandException extends RuntimeException {
    public ExternalCommandException(final String message) {
        super(message);
    }

    public ExternalCommandException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
