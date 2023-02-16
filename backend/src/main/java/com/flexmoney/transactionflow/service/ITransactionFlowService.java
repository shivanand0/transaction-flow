package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.*;
import org.springframework.http.ResponseEntity;

public interface ITransactionFlowService {
    ResponseEntity<UserResponse> saveUser(UserDTO userDTO);
    ResponseEntity<?> saveTrackStage(Long trackId, TrackStageDTO trackStageDTO);
    LenderInfoModel addLender(LenderInfoDTO lenderInfoDTO);
    Details getDetails();
}
