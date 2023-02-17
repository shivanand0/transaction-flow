package com.flexmoney.transactionflow.repository;

import com.flexmoney.transactionflow.model.TrackStageModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Transactional
public interface ITrackStageRepository extends JpaRepository<TrackStageModel,Integer> {
    @Modifying
    @Query(value = "UPDATE TrackStageModel t SET t.selection=:selection,t.selectedLenderId = :selectedLenderId, t.selectedTenureId = :selectedTenureId WHERE t.trackId = :id")
    void updateRemainingFieldsById(@Param("selection") TrackStageModel.selectionStage selection, @Param("id") UUID id, @Param("selectedLenderId") Integer selectedLenderId, @Param("selectedTenureId") Integer selectedTenureId);

    TrackStageModel findByUserId(Long userId);
}
