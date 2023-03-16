package com.flexmoney.transactionflow.repository;


import com.flexmoney.transactionflow.model.ELender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILenderRepository extends JpaRepository<ELender,Integer> {
    ELender findByLenderName(String lenderName);
}
