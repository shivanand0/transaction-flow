package com.flexmoney.transactionflow.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleException() {
        String message="Internal Server Error";
        ErrorDetails errorDetailsResponse = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
        return new ResponseEntity<>(errorDetailsResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message;
        message= Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        ErrorDetails errorDetailsResponse = new ErrorDetails(HttpStatus.BAD_REQUEST.value(),message);
        return new ResponseEntity<>(errorDetailsResponse, HttpStatus.BAD_REQUEST);
    }
}