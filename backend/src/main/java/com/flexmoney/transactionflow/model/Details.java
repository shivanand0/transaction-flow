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
public class Details {

    private String userName;

    private String mobileNumber;

    private double amount;
    private List<LenderDetails> lenderDetailsList;

}
