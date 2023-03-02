package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.*;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ITransactionFlowService {
    ResponseEntity<UserResponseModel> saveUser(UserRequestModel userRequestModel) throws Exception;
    ResponseEntity<?> saveTrackStage(UUID trackId, TrackStageRequestModel trackStageRequestModel);
    LenderInfoModel addLender(LenderInfoRequestModel lenderInfoRequestModel);
    ResponseEntity<DetailsModel> getDetails(UUID detailsId);

    ResponseEntity<TransactionResponse> InitiateTxn(TransactionRequestModel transactionRequestModel);

    ResponseEntity<TransactionResponse> ConfirmTxn(TransactionRequestModel transactionRequestModel);
}
