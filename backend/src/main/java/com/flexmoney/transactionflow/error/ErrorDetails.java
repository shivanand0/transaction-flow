package com.flexmoney.transactionflow.error;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Data
public class ErrorDetails {
    private int errorCode;
    private String errorTitle;
    private String errorMessage;
    private int statusCode;

    public ErrorDetails(int errorCode,String errorTitle, String errorMessage,int statusCode) {
        this.errorCode=errorCode;
        this.errorTitle=errorTitle;
        this.errorMessage=errorMessage;
        this.statusCode=statusCode;
    }
}
