package com.flexmoney.transactionflow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmiDetails {
    private Integer loanDuration;
    private double interestRate;
    private double monthlyInstallment;
    private double totalInterest;
    private double loanAmount;
    private String tenureType;
}
