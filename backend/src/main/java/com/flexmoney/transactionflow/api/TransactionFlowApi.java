package com.flexmoney.transactionflow.api;
import com.flexmoney.transactionflow.model.Details;
import com.flexmoney.transactionflow.model.LenderInfoDTO;
import com.flexmoney.transactionflow.model.LenderInfoModel;
import com.flexmoney.transactionflow.model.UserDTO;
import com.flexmoney.transactionflow.service.ITransactionFlowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionFlowApi {
        @Autowired
        private ITransactionFlowService transactionFlowService;


        @PostMapping("/users")
        public ResponseEntity<UserDTO> saveUser(@Valid @RequestBody UserDTO userDTO){
            return transactionFlowService.saveUser(userDTO);
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
        public Details getDetails(){
                return transactionFlowService.getDetails();
        }
}