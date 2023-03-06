package com.flexmoney.transactionflow.api;
import com.flexmoney.transactionflow.model.*;
import com.flexmoney.transactionflow.service.ILenderService;
import com.flexmoney.transactionflow.service.ITrackStageService;
import com.flexmoney.transactionflow.service.ITransactionFlowService;
import com.flexmoney.transactionflow.service.IUserService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
public class TransactionFlowApi {
        @Autowired
        private IUserService userService;

        @Autowired
        private ILenderService lenderService;

        @Autowired
        private ITrackStageService trackStageService;

        @Autowired
        private ITransactionFlowService transactionFlowService;

        @PostMapping("/users")
        public ResponseEntity<?> saveUser(@Valid @RequestBody UserRequestModel userRequestModel, BindingResult bindingResult) throws Exception {
                if (bindingResult.hasErrors())
                {
                        ResponseEntity.badRequest().body(bindingResult);
                }
                return userService.saveUser(userRequestModel);
        }

        @PostMapping("trackStage/{detailsId}")
        public ResponseEntity<?> saveTrackStage(@Valid @PathVariable("detailsId") UUID detailsId, @Valid @RequestBody TrackStageRequestModel trackStageRequestModel){
                return trackStageService.saveTrackStage(detailsId, trackStageRequestModel);
        }
        @PostMapping("/addLender")
        public ResponseEntity<?> addLender(@RequestBody LenderInfoRequestModel lenderInfoRequestModel){
                LenderInfoModel lenderInfoModel =lenderService.addLender(lenderInfoRequestModel);
                if(lenderInfoModel==null){
                        return new ResponseEntity<>("Error Occurred !!! Lender Details not added", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>("Lender Details added Successfully",HttpStatus.CREATED);
        }

        @PostMapping("/details")
        public ResponseEntity<DetailsModel> getDetails(@RequestParam UUID uuid){
                return lenderService.getDetails(uuid);
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