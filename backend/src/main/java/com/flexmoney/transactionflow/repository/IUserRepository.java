package com.flexmoney.transactionflow.repository;
import com.flexmoney.transactionflow.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<UserModel,Long>{
}
