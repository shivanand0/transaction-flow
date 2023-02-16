package com.flexmoney.transactionflow.api;
import com.flexmoney.transactionflow.model.UserDTO;
import com.flexmoney.transactionflow.service.ITransactionFlowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionFlowApi {
        @Autowired
        private ITransactionFlowService userService;

        @PostMapping("/users")
        public ResponseEntity<UserDTO> saveUser(@Valid @RequestBody UserDTO userDTO){
            return userService.saveUser(userDTO);
        }
}