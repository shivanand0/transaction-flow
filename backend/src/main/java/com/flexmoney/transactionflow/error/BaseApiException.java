package com.flexmoney.transactionflow.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseApiException extends RuntimeException{
    private final int code;
    private final String title;
    private final String message;
}
