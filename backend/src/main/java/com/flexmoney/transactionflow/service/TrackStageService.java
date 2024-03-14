package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.error.TrackStageException;
import com.flexmoney.transactionflow.model.EssentialDetailsModel;
import com.flexmoney.transactionflow.model.TrackStageModel;
import com.flexmoney.transactionflow.model.TrackStageRequestModel;
import com.flexmoney.transactionflow.repository.IEssentialDetailsRepository;
import com.flexmoney.transactionflow.repository.ITrackStageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class TrackStageService implements ITrackStageService {
    @Autowired
    private ITrackStageRepository trackStageRepository;
    @Autowired
    private IEssentialDetailsRepository essentialDetailsRepository;

    @Override
    public void saveTrackStage(UUID detailsId, TrackStageRequestModel trackStageRequestModel) throws TrackStageException {
        EssentialDetailsModel essentialDetailsModel = essentialDetailsRepository.findById(detailsId).get();
        UUID trackId = essentialDetailsModel.getDetailsId();
        TrackStageModel.selectionStage selection = trackStageRequestModel.getSelection();
        Integer selectedLenderId = trackStageRequestModel.getSelectedLenderId();
        Integer selectedLenderInfoId = trackStageRequestModel.getSelectedLenderInfoId();
        TrackStageModel trackStageModel = trackStageRepository.updateRemainingFieldsById(selection, trackId, selectedLenderId, selectedLenderInfoId);
        if (trackStageModel == null) {
            log.error("Error while updating trackStage with detailsId: {}", detailsId);
            throw new TrackStageException("System Error! We are experiencing technical difficulties & are working to fix them. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
