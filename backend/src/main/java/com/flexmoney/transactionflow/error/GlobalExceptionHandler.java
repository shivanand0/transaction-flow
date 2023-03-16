package com.flexmoney.transactionflow.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleException(Exception e) {
        String message = "Internal Server Error";
        ErrorDetails errorDetailsResponse = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return new ResponseEntity<>(errorDetailsResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}