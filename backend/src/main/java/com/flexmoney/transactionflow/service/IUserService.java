package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.TrackStageDTO;
import com.flexmoney.transactionflow.model.UserDTO;
import com.flexmoney.transactionflow.model.UserResponse;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface IUserService {
    ResponseEntity<UserResponse> saveUser(UserDTO userDTO) throws Exception;

}
