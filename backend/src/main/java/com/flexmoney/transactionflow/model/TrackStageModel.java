package com.flexmoney.transactionflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
public class TrackStageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID trackId;
    @NotNull(message = "Please send the userId")
    private Long userId;
    @NotNull(message = "Please send the amount")
    private double amount;
    public enum selectionStage{LENDER_SELECTION,TENURE_SELECTION,TWO_FACTOR_AUTHENTICATION,TRANSACTION_COMPLETE}
    @Enumerated(EnumType.STRING)
    private selectionStage selection;
    private Integer selectedLenderId;
    private Integer selectedLenderInfoId;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
}
