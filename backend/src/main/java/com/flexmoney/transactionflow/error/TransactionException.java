package com.flexmoney.transactionflow.error;

import lombok.Getter;

@Getter
public class TransactionException extends Exception {
    private final int code;

    public TransactionException(String exceptionMsg, int code) {
        super(exceptionMsg);
        this.code = code;
    }
}
