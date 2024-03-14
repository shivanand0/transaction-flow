package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.error.TransactionException;
import com.flexmoney.transactionflow.model.TransactionRequestModel;
import com.flexmoney.transactionflow.model.TransactionResponse;


public interface ITransactionFlowService {

    TransactionResponse initiateTxn(TransactionRequestModel transactionRequestModel) throws TransactionException;

    TransactionResponse confirmTxn(TransactionRequestModel transactionRequestModel) throws TransactionException;

}
