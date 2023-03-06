package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.UserRequestModel;
import com.flexmoney.transactionflow.model.UserResponseModel;
import org.springframework.http.ResponseEntity;

public interface IUserService {

    ResponseEntity<UserResponseModel> saveUser(UserRequestModel userRequestModel) throws Exception;

}
