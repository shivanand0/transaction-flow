package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.UserRequestModel;
import com.flexmoney.transactionflow.model.UserResponseModel;

public interface IUserService {

    UserResponseModel saveUser(UserRequestModel userRequestModel) throws Exception;

}
