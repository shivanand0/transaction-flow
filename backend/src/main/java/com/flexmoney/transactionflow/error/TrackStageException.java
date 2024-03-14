package com.flexmoney.transactionflow.error;

import lombok.Getter;

@Getter
public class TrackStageException extends Exception {

    private final int code;

    public TrackStageException(String exceptionMsg, int code) {
        super(exceptionMsg);
        this.code = code;
    }
}
