package com.flexmoney.transactionflow.core.api;
import com.flexmoney.transactionflow.core.error.UserException;
import com.flexmoney.transactionflow.model.*;
import com.flexmoney.transactionflow.service.*;
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
        private IUserService userService;
       
        @Autowired
        private ITrackStageService trackStageService;
        
        @Autowired
        private ILenderService lenderService;
        
        @Autowired
        private ITransactionService transactionService;

        @PostMapping("/users")
        public ResponseEntity<UserResponse> saveUser(@Valid @RequestBody UserDTO userDTO) throws Exception {
            return userService.saveUser(userDTO);
        }

        @PostMapping("trackStage/{detailsId}")
        public ResponseEntity<?> saveTrackStage(@Valid @PathVariable("detailsId") UUID detailsId, @Valid @RequestBody TrackStageDTO trackStageDTO) throws Exception{
                return trackStageService.saveTrackStage(detailsId,trackStageDTO);
        }
        @PostMapping("/addLender")
        public ResponseEntity<?> addLender(@RequestBody LenderInfoDTO lenderInfoDTO) throws Exception{
                ELenderInfo ELenderInfo =lenderService.addLender(lenderInfoDTO);
                if(ELenderInfo ==null){
                        return new ResponseEntity<>("Error Occurred !!! Lender Details not added", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>("Lender Details added Successfully",HttpStatus.CREATED);
        }

        @PostMapping("/details")
        public ResponseEntity<Details> getDetails(@RequestParam UUID uuid) throws Exception{
                return transactionService.getDetails(uuid);
        }

        @PostMapping("/transaction/initiate")
        public ResponseEntity<TransactionResponse> initiateTxn(@RequestBody TransactionDTO transactionDTO) throws Exception{
                return transactionService.InitiateTxn(transactionDTO);
        }

        @PostMapping("/transaction/confirm")
        public ResponseEntity<TransactionResponse> confirmTxn(@RequestBody TransactionDTO transactionDTO) throws Exception{
                return transactionService.ConfirmTxn(transactionDTO);
        }
}