package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.Details;
import com.flexmoney.transactionflow.model.LenderInfoDTO;
import com.flexmoney.transactionflow.model.LenderInfoModel;
import com.flexmoney.transactionflow.model.UserDTO;
import org.springframework.http.ResponseEntity;

public interface ITransactionFlowService {
    ResponseEntity<UserDTO> saveUser(UserDTO userDTO);
    LenderInfoModel addLender(LenderInfoDTO lenderInfoDTO);
    Details getDetails();


}
