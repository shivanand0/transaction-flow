package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.ETrackStage;
import com.flexmoney.transactionflow.model.EUserVerificationInfo;
import com.flexmoney.transactionflow.model.TrackStageDTO;
import com.flexmoney.transactionflow.repository.IUserVerificationInfoRepository;
import com.flexmoney.transactionflow.repository.ITrackStageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class TrackStageService implements ITrackStageService{
    @Autowired
    private IUserVerificationInfoRepository essentialDetailsRepository;
    @Autowired
    private ITrackStageRepository trackStageRepository;
    @Override
    public ResponseEntity<?> saveTrackStage(UUID detailsId, TrackStageDTO trackStageDTO) {
        EUserVerificationInfo EUserVerificationInfo = essentialDetailsRepository.findById(detailsId).get();
        UUID trackId = EUserVerificationInfo.getTrackId();
        ETrackStage.selectionStage selection = trackStageDTO.getSelection();
        Integer selectedLenderId = trackStageDTO.getSelectedLenderId();
        Integer selectedLenderInfoId = trackStageDTO.getSelectedLenderInfoId();
        trackStageRepository.updateRemainingFieldsById(selection, trackId, selectedLenderId, selectedLenderInfoId);
        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }
}
