package com.leafyjava.pannellumtourmaker.exceptions;

public class UnsupportedFileExtensionException extends RuntimeException {
    public UnsupportedFileExtensionException(final String message) {
        super(message);
    }

    public UnsupportedFileExtensionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
