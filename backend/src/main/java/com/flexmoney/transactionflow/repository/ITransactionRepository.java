package com.flexmoney.transactionflow.repository;

import com.flexmoney.transactionflow.model.TransactionModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Transactional
public interface ITransactionRepository extends JpaRepository<TransactionModel, UUID> {
    TransactionModel findByDetailsId(UUID detailsId);

    @Modifying
    @Query(value = "UPDATE TransactionModel t SET t.status=:status WHERE t.txnId = :id")
    void updateFieldsById(@Param("id") UUID id, @Param("status") String status);
}
