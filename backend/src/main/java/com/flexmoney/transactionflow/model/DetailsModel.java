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
public class DetailsModel {

    private Integer statusCode;
    private boolean status;
    private String message;
    private String userName;

    private String mobileNumber;

    private double amount;
    private List<LenderDetailsModel> lenderDetailsModelList;

}
