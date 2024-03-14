package com.flexmoney.transactionflow.repository;


import com.flexmoney.transactionflow.model.LenderModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILenderRepository extends JpaRepository<LenderModel, Integer> {
    LenderModel findByLenderName(String lenderName);
}
