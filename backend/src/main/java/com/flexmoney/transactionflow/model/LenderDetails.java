package com.flexmoney.transactionflow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LenderDetails {
    private Integer lenderId;
    private String lenderName;
    private String lenderType;
    private List<EmiDetails> emiDetailsList;
}
