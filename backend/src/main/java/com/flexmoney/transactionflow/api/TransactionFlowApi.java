package com.flexmoney.transactionflow.api;
import com.flexmoney.transactionflow.model.*;
import com.flexmoney.transactionflow.service.ITransactionFlowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
public class TransactionFlowApi {
        @Autowired
        private ITransactionFlowService transactionFlowService;

        @PostMapping("/users")
        public ResponseEntity<UserResponseModel> saveUser(@Valid @RequestBody UserRequestModel userRequestModel) throws Exception {
            return transactionFlowService.saveUser(userRequestModel);
        }

        @PostMapping("trackStage/{detailsId}")
        public ResponseEntity<?> saveTrackStage(@Valid @PathVariable("detailsId") UUID detailsId, @Valid @RequestBody TrackStageRequestModel trackStageRequestModel){
                return transactionFlowService.saveTrackStage(detailsId, trackStageRequestModel);
        }
        @PostMapping("/addLender")
        public ResponseEntity<?> addLender(@RequestBody LenderInfoRequestModel lenderInfoRequestModel){
                LenderInfoModel lenderInfoModel =transactionFlowService.addLender(lenderInfoRequestModel);
                if(lenderInfoModel==null){
                        return new ResponseEntity<>("Error Occurred !!! Lender Details not added", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>("Lender Details added Successfully",HttpStatus.CREATED);
        }

        @PostMapping("/details")
        public ResponseEntity<DetailsModel> getDetails(@RequestParam UUID uuid){
                return transactionFlowService.getDetails(uuid);
        }

        @PostMapping("/transaction/initiate")
        public ResponseEntity<TransactionResponse> initiateTxn(@RequestBody TransactionRequestModel transactionRequestModel){
                return transactionFlowService.InitiateTxn(transactionRequestModel);
        }

        @PostMapping("/transaction/confirm")
        public ResponseEntity<TransactionResponse> confirmTxn(@RequestBody TransactionRequestModel transactionRequestModel){
                return transactionFlowService.ConfirmTxn(transactionRequestModel);
        }
}