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
public class LenderDetailsModel {
    private Integer lenderId;
    private String lenderName;
    private String primaryLogoUrl;

    private String secondaryLogoUrl;
    private String lenderType;
    private List<EmiDetailsModel> emiDetailsModelList;
}
