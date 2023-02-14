package com.flexmoney.transactionflow.service;
import com.flexmoney.transactionflow.dto.UserDTO;
import com.flexmoney.transactionflow.model.UserModel;

public interface UserService{

    public UserDTO saveUser(UserDTO userDTO);
}
