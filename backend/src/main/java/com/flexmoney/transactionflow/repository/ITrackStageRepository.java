package com.flexmoney.transactionflow.repository;

import com.flexmoney.transactionflow.model.ETrackStage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Transactional
public interface ITrackStageRepository extends JpaRepository<ETrackStage,Integer> {
    @Modifying
    @Query(value = "UPDATE ETrackStage t SET t.selection=:selection,t.selectedLenderId = :selectedLenderId, t.selectedLenderInfoId = :selectedLenderInfoId WHERE t.trackId = :id")
    void updateRemainingFieldsById(@Param("selection") ETrackStage.selectionStage selection, @Param("id") UUID id, @Param("selectedLenderId") Integer selectedLenderId, @Param("selectedLenderInfoId") Integer selectedLenderInfoId);

    ETrackStage findByTrackId(UUID trackId);
}
