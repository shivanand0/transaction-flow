package com.flexmoney.transactionflow.controller;
import com.flexmoney.transactionflow.dto.UserDTO;
import com.flexmoney.transactionflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public UserDTO saveUser(@Valid @RequestBody UserDTO userDTO){
        return userService.saveUser(userDTO);
    }
}
