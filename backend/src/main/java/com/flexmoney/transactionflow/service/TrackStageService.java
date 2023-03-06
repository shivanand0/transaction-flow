package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.EssentialDetailsModel;
import com.flexmoney.transactionflow.model.TrackStageModel;
import com.flexmoney.transactionflow.model.TrackStageRequestModel;
import com.flexmoney.transactionflow.repository.IEssentialDetailsRepository;
import com.flexmoney.transactionflow.repository.ITrackStageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class TrackStageService implements ITrackStageService{
    @Autowired
    private ITrackStageRepository trackStageRepository;
    @Autowired
    private IEssentialDetailsRepository essentialDetailsRepository;
    @Override
    public ResponseEntity<?> saveTrackStage(UUID detailsId, TrackStageRequestModel trackStageRequestModel) {
        EssentialDetailsModel essentialDetailsModel = essentialDetailsRepository.findById(detailsId).get();
        UUID trackId = essentialDetailsModel.getDetailsId();
        TrackStageModel.selectionStage selection = trackStageRequestModel.getSelection();
        Integer selectedLenderId = trackStageRequestModel.getSelectedLenderId();
        Integer selectedLenderInfoId = trackStageRequestModel.getSelectedLenderInfoId();
        trackStageRepository.updateRemainingFieldsById(selection, trackId, selectedLenderId, selectedLenderInfoId);
        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

}
