package com.flexmoney.transactionflow.repository;


import com.flexmoney.transactionflow.model.LenderInfoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILenderInfoRepository extends JpaRepository<LenderInfoModel,Integer> {
    List<LenderInfoModel> findAllByLenderId(Integer id);
}
