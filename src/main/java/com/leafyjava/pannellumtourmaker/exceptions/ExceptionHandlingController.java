package com.leafyjava.pannellumtourmaker.exceptions;

import com.leafyjava.pannellumtourmaker.storage.exceptions.StorageFileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<ExceptionResponse> storageFileNotFound(StorageFileNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        response.setMessage(ex.getMessage());

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnsupportedFileExtensionException.class)
    public ResponseEntity<ExceptionResponse> unsupportedFileFor(UnsupportedFileExtensionException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setMessage(ex.getMessage());

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }
}
