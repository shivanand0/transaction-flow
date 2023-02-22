package com.flexmoney.transactionflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TwoFVerificationResponse {
    private boolean status;
    private Integer statusCode;
    private String message;
}
