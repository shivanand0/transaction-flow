package com.flexmoney.transactionflow.service;
import com.flexmoney.transactionflow.dto.UserDTO;
import com.flexmoney.transactionflow.model.UserModel;
import com.flexmoney.transactionflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        UserModel user=new UserModel();
        user.setUserName(userDTO.getUserName());
        user.setMobileNumber(userDTO.getMobileNumber());
        user.setCreditLimit(50000.00);
        user.setLastFourDigitsOfPan(1234L);
        user.setLenderId(Arrays.asList(1,2,3));
        userRepository.save(user);
        return userDTO;
    }
}
