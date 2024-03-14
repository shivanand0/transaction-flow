package com.flexmoney.transactionflow.error;

import lombok.Getter;

@Getter
public class LenderException extends Exception {
    private final int code;

    public LenderException(String exceptionMsg, int code) {
        super(exceptionMsg);
        this.code = code;
    }
}
