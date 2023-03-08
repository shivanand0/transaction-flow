package com.flexmoney.transactionflow.error;

import lombok.Getter;

@Getter
public class UserException extends Exception {

    private final int code;

    public UserException(String exceptionMsg, int code) {
        super(exceptionMsg);
        this.code = code;
    }
}
