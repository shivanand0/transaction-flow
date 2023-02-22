package com.flexmoney.transactionflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    // private UUID txnId;
    // private double amount;
    // private String userName;
    // private String mobileNumber;
    private boolean status;
    private Integer statusCode;
    private String message;
}
