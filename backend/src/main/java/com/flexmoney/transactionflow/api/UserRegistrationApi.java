package com.flexmoney.transactionflow.api;

import com.flexmoney.transactionflow.error.ErrorDetails;
import com.flexmoney.transactionflow.error.UserException;
import com.flexmoney.transactionflow.error.ValidationException;
import com.flexmoney.transactionflow.model.UserRequestModel;
import com.flexmoney.transactionflow.model.UserResponseModel;
import com.flexmoney.transactionflow.service.IUserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Slf4j
public class UserRegistrationApi {

    @Autowired
    private IUserService userService;

    @PostMapping("/users")
    public ResponseEntity<?> saveUser(@Valid @RequestBody UserRequestModel userRequestModel, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            FieldError fieldError = result.getFieldErrors().get(0);
            log.error("User validation failed, fields: {}", result.getFieldErrors());
            throw new ValidationException(fieldError.getDefaultMessage(), 400);
        }
        UserResponseModel userResponseModel = userService.saveUser(userRequestModel);
        return new ResponseEntity<>(userResponseModel, HttpStatus.CREATED);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleInvalidFields(ValidationException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> handleUserException(UserException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
