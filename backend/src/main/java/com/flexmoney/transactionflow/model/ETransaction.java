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
public class ETransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID txnId;

    @NotNull(message = "detailsId required")
    private UUID detailsId;

    @NotNull(message = "userId required")
    private Long userId;

    @NotNull(message = "lenderInfoId required")
    private Integer lenderInfoId;

    /*
    // lenderInfoId will give lenderId, tenureId, rateOfInterest
    private long lenderId;
    private long tenureId;
    private double rateOfInterest;
    // trackId can fetch amount
    private double amount;
     */
    private String status;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
