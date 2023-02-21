package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.*;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ITransactionFlowService {
    ResponseEntity<UserResponse> saveUser(UserDTO userDTO) throws Exception;
    ResponseEntity<?> saveTrackStage(UUID trackId, TrackStageDTO trackStageDTO);
    LenderInfoModel addLender(LenderInfoDTO lenderInfoDTO);
    Details getDetails(UUID detailsId);

    ResponseEntity<TwoFVerificationResponse> OtpVerification(String verificationType, TwoFVerificationDTO twoFVerificationDTO);


    ResponseEntity<TransactionResponse> InitTransaction(TransactionDTO transactionDTO);
}
