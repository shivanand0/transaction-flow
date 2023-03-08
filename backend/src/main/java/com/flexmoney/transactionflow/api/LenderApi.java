package com.flexmoney.transactionflow.api;

import com.flexmoney.transactionflow.error.ErrorDetails;
import com.flexmoney.transactionflow.error.LenderException;
import com.flexmoney.transactionflow.model.DetailsModel;
import com.flexmoney.transactionflow.model.LenderInfoRequestModel;
import com.flexmoney.transactionflow.service.ILenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
public class LenderApi {
    @Autowired
    private ILenderService lenderService;

    @PostMapping("/addLender")
    public ResponseEntity<?> addLender(@RequestBody LenderInfoRequestModel lenderInfoRequestModel) throws LenderException {
        lenderService.addLender(lenderInfoRequestModel);
        return new ResponseEntity<>("Lender Details added Successfully", HttpStatus.CREATED);
    }

    @PostMapping("/details")
    public ResponseEntity<DetailsModel> getDetails(@RequestParam UUID uuid) {
        return lenderService.getDetails(uuid);
    }

    @ExceptionHandler(LenderException.class)
    public ResponseEntity<?> handleUserException(LenderException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
