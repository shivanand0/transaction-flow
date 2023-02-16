package com.flexmoney.transactionflow.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackStageDTO {

    @Enumerated(EnumType.STRING)
    private TrackStageModel.selectionStage selection;
    private Integer selectedLenderId;
    private Integer selectedTenureId;
}
