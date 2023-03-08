package com.flexmoney.transactionflow.api;

import com.flexmoney.transactionflow.model.TrackStageRequestModel;
import com.flexmoney.transactionflow.model.TransactionRequestModel;
import com.flexmoney.transactionflow.model.TransactionResponse;
import com.flexmoney.transactionflow.service.ITrackStageService;
import com.flexmoney.transactionflow.service.ITransactionFlowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
public class TransactionFlowApi {

    @Autowired
    private ITrackStageService trackStageService;

    @Autowired
    private ITransactionFlowService transactionFlowService;


    @PostMapping("trackStage/{detailsId}")
    public ResponseEntity<?> saveTrackStage(@Valid @PathVariable("detailsId") UUID detailsId, @Valid @RequestBody TrackStageRequestModel trackStageRequestModel) {
        return trackStageService.saveTrackStage(detailsId, trackStageRequestModel);
    }

    @PostMapping("/transaction/initiate")
    public ResponseEntity<TransactionResponse> initiateTxn(@RequestBody TransactionRequestModel transactionRequestModel) {
        return transactionFlowService.InitiateTxn(transactionRequestModel);
    }

    @PostMapping("/transaction/confirm")
    public ResponseEntity<TransactionResponse> confirmTxn(@RequestBody TransactionRequestModel transactionRequestModel) {
        return transactionFlowService.ConfirmTxn(transactionRequestModel);
    }


}