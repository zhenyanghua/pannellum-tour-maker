package com.leafyjava.pannellumtourmaker.exceptions;

public class UnsupportedFileTreeException extends RuntimeException {
    public UnsupportedFileTreeException(final String message) {
        super(message);
    }

    public UnsupportedFileTreeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
