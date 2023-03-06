package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.TrackStageRequestModel;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ITrackStageService {
    ResponseEntity<?> saveTrackStage(UUID trackId, TrackStageRequestModel trackStageRequestModel);

}
