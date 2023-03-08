package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.TransactionRequestModel;
import com.flexmoney.transactionflow.model.TransactionResponse;
import org.springframework.http.ResponseEntity;


public interface ITransactionFlowService {

    ResponseEntity<TransactionResponse> InitiateTxn(TransactionRequestModel transactionRequestModel);

    ResponseEntity<TransactionResponse> ConfirmTxn(TransactionRequestModel transactionRequestModel);

}
