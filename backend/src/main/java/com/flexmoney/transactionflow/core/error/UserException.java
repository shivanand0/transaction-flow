package com.flexmoney.transactionflow.core.error;

import com.flexmoney.transactionflow.error.BaseApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class UserException extends BaseApiException {
    private final HttpStatus status;

    public UserException(int code, String message) {
        this(code,"Error", message, HttpStatus.OK);
    }
    public UserException(int code, String message, HttpStatus status) {

        this(code, "Error", message, status);
    }
    public UserException(int code, String title, String message, HttpStatus status) {
        super(code, title, message);
        this.status = status;
    }
}
