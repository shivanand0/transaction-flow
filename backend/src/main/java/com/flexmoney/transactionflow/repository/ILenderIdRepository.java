package com.flexmoney.transactionflow.repository;

import com.flexmoney.transactionflow.model.ELenderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILenderIdRepository extends JpaRepository<ELenderId,Integer> {

    List<ELenderId> findAllByUserMobileNumber(String i);
}
