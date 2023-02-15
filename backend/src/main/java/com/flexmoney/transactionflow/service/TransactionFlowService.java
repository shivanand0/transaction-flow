package com.flexmoney.transactionflow.service;
import com.flexmoney.transactionflow.model.UserDTO;
import com.flexmoney.transactionflow.model.UserModel;
import com.flexmoney.transactionflow.repository.ITransactionFlowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class TransactionFlowService implements ITransactionFlowService {
        @Autowired
        private ITransactionFlowRepository ITransactionFlowRepository;

        @Override
        public ResponseEntity<UserDTO> saveUser(UserDTO userDTO){
                UserModel user = new UserModel();
                user.setUserName(userDTO.getUserName());
                user.setMobileNumber(userDTO.getMobileNumber());
                user.setCreditLimit(50000.00);
                user.setLastFourDigitsOfPan(1234L);
                user.setLenderId(Arrays.asList(1, 2, 3));
                ITransactionFlowRepository.save(user);
                return new ResponseEntity<>(userDTO,HttpStatus.CREATED);
        }
}
