package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.error.TrackStageException;
import com.flexmoney.transactionflow.model.TrackStageRequestModel;

import java.util.UUID;

public interface ITrackStageService {
    void saveTrackStage(UUID trackId, TrackStageRequestModel trackStageRequestModel) throws TrackStageException;

}
