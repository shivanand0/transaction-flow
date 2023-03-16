package com.flexmoney.transactionflow.api;

import com.flexmoney.transactionflow.error.ErrorDetails;
import com.flexmoney.transactionflow.error.TrackStageException;
import com.flexmoney.transactionflow.error.TransactionException;
import com.flexmoney.transactionflow.error.ValidationException;
import com.flexmoney.transactionflow.model.TransactionRequestModel;
import com.flexmoney.transactionflow.model.TransactionResponse;
import com.flexmoney.transactionflow.service.ITransactionFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Slf4j
public class TransactionFlowApi {

    @Autowired
    private ITransactionFlowService transactionFlowService;

    @PostMapping("/transaction/initiate")
    public ResponseEntity<?> initiateTxn(@RequestBody TransactionRequestModel transactionRequestModel, BindingResult result) throws ValidationException, TransactionException {
        if (result.hasErrors()) {
            FieldError fieldError = result.getFieldErrors().get(0);
            log.error("transactionRequestModel validation failed, fields: {}", result.getFieldErrors());
            throw new ValidationException(fieldError.getDefaultMessage(), 400);
        }
        TransactionResponse transactionResponse = transactionFlowService.initiateTxn(transactionRequestModel);
        return new ResponseEntity<>(transactionResponse, HttpStatus.CREATED);
    }

    @PostMapping("/transaction/confirm")
    public ResponseEntity<?> confirmTxn(@RequestBody TransactionRequestModel transactionRequestModel, BindingResult result) throws ValidationException, TransactionException {
        if (result.hasErrors()) {
            FieldError fieldError = result.getFieldErrors().get(0);
            log.error("transactionRequestModel validation failed, fields: {}", result.getFieldErrors());
            throw new ValidationException(fieldError.getDefaultMessage(), 400);
        }
        TransactionResponse transactionResponse = transactionFlowService.confirmTxn(transactionRequestModel);
        return new ResponseEntity<>(transactionResponse, HttpStatus.ACCEPTED);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleInvalidFields(ValidationException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TrackStageException.class)
    public ResponseEntity<?> handleTransactionException(TransactionException ex) {
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setStatus(false);
        transactionResponse.setStatusCode(ex.getCode());
        transactionResponse.setMessage(ex.getMessage());

        return new ResponseEntity<>(transactionResponse, HttpStatusCode.valueOf(ex.getCode()));
    }

}