package com.flexmoney.transactionflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestModel {
    private UUID detailsId;
    private Long otp;
    private String status;
    private String remark;
}
