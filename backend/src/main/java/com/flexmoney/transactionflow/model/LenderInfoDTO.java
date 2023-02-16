package com.flexmoney.transactionflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LenderInfoDTO {
    private LenderModel lender;
    private Integer tenure;
    private double rateOfInterest;
    private String tenureType;
}
