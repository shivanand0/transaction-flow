package com.flexmoney.transactionflow.error;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ErrorDetails {
    private int statusCode;
    private String errorMessage;

    public ErrorDetails(int statusCode, String context) {
        this.statusCode = statusCode;
        this.errorMessage = context;
    }
}
