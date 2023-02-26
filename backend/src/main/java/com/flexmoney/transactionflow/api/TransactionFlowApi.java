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
        public ResponseEntity<UserResponse> saveUser(@Valid @RequestBody UserDTO userDTO) throws Exception {
            return transactionFlowService.saveUser(userDTO);
        }

        @PostMapping("trackStage/{detailsId}")
        public ResponseEntity<?> saveTrackStage(@Valid @PathVariable("detailsId") UUID detailsId, @Valid @RequestBody TrackStageDTO trackStageDTO){
                return transactionFlowService.saveTrackStage(detailsId,trackStageDTO);
        }
        @PostMapping("/addLender")
        public ResponseEntity<?> addLender(@RequestBody LenderInfoDTO lenderInfoDTO){
                LenderInfoModel lenderInfoModel =transactionFlowService.addLender(lenderInfoDTO);
                if(lenderInfoModel==null){
                        return new ResponseEntity<>("Error Occurred !!! Lender Details not added", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>("Lender Details added Successfully",HttpStatus.CREATED);
        }

        @PostMapping("/details")
        public ResponseEntity<Details> getDetails(@RequestParam UUID uuid){
                return transactionFlowService.getDetails(uuid);
        }

        @PostMapping("/transaction/initiate")
        public ResponseEntity<TransactionResponse> initiateTxn(@RequestBody TransactionDTO transactionDTO){
                return transactionFlowService.InitiateTxn(transactionDTO);
        }

        @PostMapping("/transaction/confirm")
        public ResponseEntity<TransactionResponse> confirmTxn(@RequestBody TransactionDTO transactionDTO){
                return transactionFlowService.ConfirmTxn(transactionDTO);
        }
}