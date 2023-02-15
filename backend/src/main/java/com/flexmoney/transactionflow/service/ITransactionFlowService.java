package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.UserDTO;
import org.springframework.http.ResponseEntity;

public interface ITransactionFlowService {
    ResponseEntity<UserDTO> saveUser(UserDTO userDTO);

}
