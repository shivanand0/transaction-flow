package com.flexmoney.transactionflow.repository;

import com.flexmoney.transactionflow.model.LenderIdModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILenderIdRepository extends JpaRepository<LenderIdModel,Integer> {
    List<LenderIdModel> findAllByLenderId(int i);

    List<LenderIdModel> findAllByUserFid(int i);
}
