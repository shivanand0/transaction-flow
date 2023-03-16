package com.flexmoney.transactionflow.error;

//difference of 2 in error codes
public enum ApiResponseCodeEnum {

    INVALID_MOBILE_NUMBER(4001),
    LENDER_NOT_FOUND(4003),
    INVALID_TRANSACTION(4005);
    private final int code;

    ApiResponseCodeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
