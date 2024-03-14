package com.flexmoney.transactionflow.error;

import lombok.Getter;


@Getter
public class ValidationException extends Exception {

    private final int code;

    public ValidationException(String exceptionMsg, int code) {
        super(exceptionMsg);
        this.code = code;
    }
}

