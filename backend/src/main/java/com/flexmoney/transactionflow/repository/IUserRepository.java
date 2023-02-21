package com.flexmoney.transactionflow.repository;
import com.flexmoney.transactionflow.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<UserModel,Long>{
    UserModel findByMobileNumber(String mobileNumber);
}

