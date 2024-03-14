package com.flexmoney.transactionflow.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackStageModel {
    @Id
    public UUID trackId;

    public enum selectionStage {LENDER_SELECTION, TENURE_SELECTION, TWO_FACTOR_AUTHENTICATION, TRANSACTION_COMPLETE}

    @Enumerated(EnumType.STRING)
    private selectionStage selection;
    private Integer selectedLenderId;
    private Integer selectedLenderInfoId;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
}
