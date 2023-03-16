package com.flexmoney.transactionflow.repository;


import com.flexmoney.transactionflow.model.ELenderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILenderInfoRepository extends JpaRepository<ELenderInfo,Integer> {
    List<ELenderInfo> findAllByLenderId(Integer id);
}
