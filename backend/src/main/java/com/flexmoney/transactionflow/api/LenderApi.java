package com.flexmoney.transactionflow.api;

import com.flexmoney.transactionflow.error.ErrorDetails;
import com.flexmoney.transactionflow.error.LenderException;
import com.flexmoney.transactionflow.error.ValidationException;
import com.flexmoney.transactionflow.model.DetailsModel;
import com.flexmoney.transactionflow.model.LenderInfoRequestModel;
import com.flexmoney.transactionflow.service.ILenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
@Slf4j
public class LenderApi {
    @Autowired
    private ILenderService lenderService;

    @PostMapping("/addLender")
    public ResponseEntity<?> addLender(@RequestBody LenderInfoRequestModel lenderInfoRequestModel, BindingResult result) throws LenderException, ValidationException {
        if (result.hasErrors()) {
            FieldError fieldError = result.getFieldErrors().get(0);
            log.error("lenderInfoRequestModel validation failed, fields: {}", result.getFieldErrors());
            throw new ValidationException(fieldError.getDefaultMessage(), 400);
        }
        lenderService.addLender(lenderInfoRequestModel);
        return new ResponseEntity<>("Lender Details added Successfully", HttpStatus.CREATED);
    }

    @PostMapping("/details")
    public ResponseEntity<?> getDetails(@RequestParam UUID uuid) throws LenderException {
        DetailsModel detailsModel = lenderService.getDetails(uuid);
        return new ResponseEntity<>(detailsModel, HttpStatus.OK);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleInvalidFields(ValidationException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LenderException.class)
    public ResponseEntity<?> handleUserException(LenderException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatusCode.valueOf(ex.getCode()));
    }

}
