package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.Details;
import com.flexmoney.transactionflow.model.TransactionDTO;
import com.flexmoney.transactionflow.model.TransactionResponse;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ITransactionService {
    ResponseEntity<Details> getDetails(UUID detailsId);

    ResponseEntity<TransactionResponse> InitiateTxn(TransactionDTO transactionDTO);

    ResponseEntity<TransactionResponse> ConfirmTxn(TransactionDTO transactionDTO);
}
