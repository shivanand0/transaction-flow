package com.flexmoney.transactionflow.api;

import com.flexmoney.transactionflow.error.ErrorDetails;
import com.flexmoney.transactionflow.error.TrackStageException;
import com.flexmoney.transactionflow.error.ValidationException;
import com.flexmoney.transactionflow.model.TrackStageRequestModel;
import com.flexmoney.transactionflow.service.ITrackStageService;
import jakarta.validation.Valid;
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
public class TrackStageApi {
    @Autowired
    private ITrackStageService trackStageService;

    @PostMapping("trackStage/{detailsId}")
    public ResponseEntity<?> saveTrackStage(@Valid @PathVariable("detailsId") UUID detailsId, @Valid @RequestBody TrackStageRequestModel trackStageRequestModel, BindingResult result) throws ValidationException, TrackStageException {
        if (result.hasErrors()) {
            FieldError fieldError = result.getFieldErrors().get(0);
            log.error("trackStageRequestModel validation failed, fields: {}", result.getFieldErrors());
            throw new ValidationException(fieldError.getDefaultMessage(), 400);
        }
        trackStageService.saveTrackStage(detailsId, trackStageRequestModel);
        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleInvalidFields(ValidationException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TrackStageException.class)
    public ResponseEntity<?> handleTrackStageException(TrackStageException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatusCode.valueOf(ex.getCode()));
    }

}

