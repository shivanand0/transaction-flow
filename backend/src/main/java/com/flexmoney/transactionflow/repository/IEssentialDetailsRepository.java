package com.flexmoney.transactionflow.repository;

import com.flexmoney.transactionflow.model.EssentialDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IEssentialDetailsRepository extends JpaRepository<EssentialDetails, UUID> {
    Optional<EssentialDetails> findById(UUID i);

    EssentialDetails findByMobileNumber(String mobileNumber);
}
