package com.flexmoney.transactionflow.repository;

import com.flexmoney.transactionflow.model.TransactionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ITransactionRepository extends JpaRepository<TransactionModel, UUID> {
    TransactionModel findByDetailsId(UUID detailsId);
}
