package com.flexmoney.transactionflow.core.error;

import com.flexmoney.transactionflow.error.BaseApiException;
import org.springframework.http.HttpStatus;

public class TransactionException extends BaseApiException {

    private final HttpStatus status;
    public TransactionException(int code, String message) {
        this(code,"Error", message, HttpStatus.OK);
    }
    public TransactionException(int code, String message, HttpStatus status) {

        this(code, "Error", message, status);
    }

    public TransactionException(int code, String title, String message, HttpStatus status) {
        super(code, title, message);
        this.status=status;
    }
}
