package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.*;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ITransactionFlowService {
    ResponseEntity<UserResponse> saveUser(UserDTO userDTO) throws Exception;
    ResponseEntity<?> saveTrackStage(UUID trackId, TrackStageDTO trackStageDTO);
    LenderInfoModel addLender(LenderInfoDTO lenderInfoDTO);
    ResponseEntity<Details> getDetails(UUID detailsId);

    ResponseEntity<TransactionResponse> InitiateTxn(TransactionDTO transactionDTO);

    ResponseEntity<TransactionResponse> ConfirmTxn(TransactionDTO transactionDTO);
}
