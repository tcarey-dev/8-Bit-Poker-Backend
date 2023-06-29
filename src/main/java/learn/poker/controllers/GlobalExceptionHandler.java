package learn.poker.controllers;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<?> handleException(DuplicateKeyException ex){
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse("Entry might already exist"),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, HttpMediaTypeException.class})
    public ResponseEntity<ErrorResponse> handleException(DataIntegrityViolationException ex) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse("Might've entered something wrong. Sorry :("),
                HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleException(DataAccessException ex) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse("We can't show you the details, but something went wrong in our database. Sorry :("),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException ex) {

        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse(ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex){
        return new ResponseEntity<String>(
                "Something went wrong on our end. Your request failed. :(",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> reportException(String message) {
        List<String> messages = List.of(message);
        return new ResponseEntity<>(messages, HttpStatus.BAD_REQUEST);
    }

}