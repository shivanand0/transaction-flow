package com.flexmoney.transactionflow.error;

public class CreditLimitException extends Exception{
    public CreditLimitException(String message){
        super(message);
    }
}
