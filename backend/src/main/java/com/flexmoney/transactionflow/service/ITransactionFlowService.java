package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.*;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ITransactionFlowService {
    ResponseEntity<UserResponse> saveUser(UserDTO userDTO);
    ResponseEntity<?> saveTrackStage(UUID trackId, TrackStageDTO trackStageDTO);
    LenderInfoModel addLender(LenderInfoDTO lenderInfoDTO);
    Details getDetails(DetailsDTO detailsDTO);
}
