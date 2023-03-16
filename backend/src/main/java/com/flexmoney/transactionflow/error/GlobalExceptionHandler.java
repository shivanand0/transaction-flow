package com.flexmoney.transactionflow.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
//        String message;
//        message= Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
//        ErrorDetails errorDetailsResponse = new ErrorDetails(HttpStatus.BAD_REQUEST.value(),message);
//        return new ResponseEntity<>(errorDetailsResponse, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(CreditLimitException.class)
//    public ResponseEntity<ErrorDetails> creditLimitException(CreditLimitException e){
//        ErrorDetails errorDetailsResponse = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), e.getMessage());
//        return new ResponseEntity<>(errorDetailsResponse, HttpStatus.BAD_REQUEST);
//    }

}