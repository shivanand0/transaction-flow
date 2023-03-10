package com.flexmoney.transactionflow.repository;

import com.flexmoney.transactionflow.model.EssentialDetails;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface IEssentialDetailsRepository extends JpaRepository<EssentialDetails, UUID> {
    Optional<EssentialDetails> findById(UUID i);

    EssentialDetails findByMobileNumber(String mobileNumber);

    @Modifying
    @Query(value = "UPDATE EssentialDetails e SET e.txnCount=:txnCount,e.PanOTPCount = :PanOTPCount, e.MobOTPCount = :MobOTPCount WHERE e.detailsId = :id")
    void updateFieldsById(@Param("txnCount") Integer txnCount, @Param("PanOTPCount") Integer PanOTPCount, @Param("MobOTPCount") Integer MobOTPCount, @Param("id") UUID id);

    @Modifying
    @Query(value = "UPDATE EssentialDetails e SET e.status=:status, e.remark = :remark WHERE e.detailsId = :id")
    void updateStatusRemarkById(@Param("id") UUID id, @Param("status") String status, @Param("remark") String remark);
}
