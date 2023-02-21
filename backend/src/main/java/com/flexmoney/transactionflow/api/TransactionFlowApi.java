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

        @PostMapping("trackStage/{trackId}")
        public ResponseEntity<?> saveTrackStage(@Valid @PathVariable("trackId") UUID trackId, @Valid @RequestBody TrackStageDTO trackStageDTO){
                return transactionFlowService.saveTrackStage(trackId,trackStageDTO);
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
        public Details getDetails(@RequestParam UUID uuid){
                return transactionFlowService.getDetails(uuid);
        }

        @PostMapping("/twoFVerification/{verificationType}")
        public ResponseEntity<TwoFVerificationResponse> OtpVerifivation(@PathVariable("verificationType") String verificationType , @RequestBody TwoFVerificationDTO twoFVerificationDTO){
                return transactionFlowService.OtpVerifivation(verificationType, twoFVerificationDTO);
        }

        @PostMapping("/initTxn")
        public ResponseEntity<TransactionResponse> initTransaction(@RequestBody TransactionDTO transactionDTO){
                return transactionFlowService.InitTransaction(transactionDTO);
        }
}