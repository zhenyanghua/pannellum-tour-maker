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
        return getResponseEntity(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnsupportedFileExtensionException.class)
    public ResponseEntity<ExceptionResponse> unsupportedFileExtension(UnsupportedFileExtensionException ex) {
        return getResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedFileTreeException.class)
    public ResponseEntity<ExceptionResponse> unsupportedFileTree(UnsupportedFileTreeException ex) {
        return getResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TourAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> tourAlreadyExists(TourAlreadyExistsException ex) {
        return getResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TourNotFoundException.class)
    public ResponseEntity<ExceptionResponse> tourNotFound(TourNotFoundException ex) {
        return getResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTourException.class)
    public ResponseEntity<ExceptionResponse> invalidTour(InvalidTourException ex) {
        return getResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ExceptionResponse> getResponseEntity(final RuntimeException ex, HttpStatus httpStatus) {
        ExceptionResponse response = new ExceptionResponse();
        response.setStatus(httpStatus.value());
        response.setError(httpStatus.getReasonPhrase());
        response.setMessage(ex.getMessage());

        return new ResponseEntity<ExceptionResponse>(response, httpStatus);
    }


}

