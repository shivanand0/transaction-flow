package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.error.UserException;
import com.flexmoney.transactionflow.model.*;
import com.flexmoney.transactionflow.repository.IEssentialDetailsRepository;
import com.flexmoney.transactionflow.repository.ILenderRepository;
import com.flexmoney.transactionflow.repository.ITrackStageRepository;
import com.flexmoney.transactionflow.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IEssentialDetailsRepository essentialDetailsRepository;

    @Autowired
    private ITrackStageRepository trackStageRepository;

    @Autowired
    private ILenderRepository lenderRepository;

    @Override
    public UserResponseModel saveUser(UserRequestModel userRequestModel) throws Exception {

        UserModel userModel = userRepository.findByMobileNumber(userRequestModel.getMobileNumber());

        if (userModel == null) {
            Long mockLastFourDigitsOfPan = 1234L;
            double mockCreditLimit = 50000.00;

            UserModel user = new UserModel();
            user.setUserName(userRequestModel.getUserName());
            user.setMobileNumber(userRequestModel.getMobileNumber());
            user.setLastFourDigitsOfPan(mockLastFourDigitsOfPan);

            List<LenderModel> lenderModelList = lenderRepository.findAll();
            if (lenderModelList == null) {
                log.error("Error while fetching the lenders");
                throw new UserException("Some error occurred please try again after some time", 500);
            }
            List<Integer> allLenderIds = lenderModelList.stream()
                    .map(lenderModel -> lenderModel.getId())
                    .collect(Collectors.toList());

            List<LenderIdModel> lenderIdModelList = new ArrayList<>();
            for (int i = 0; i < allLenderIds.size(); i++) {
                LenderIdModel lenderIdModel = LenderIdModel.builder().lenderId(allLenderIds.get(i))
                        .creditLimit(mockCreditLimit)
                        .build();
                lenderIdModelList.add(lenderIdModel);
            }
            user.setLenderId(lenderIdModelList);

            UserModel userModel1 = userRepository.save(user);
            if (userModel1 == null) {
                log.error("Unable to save the user with mobile number: " + user.getMobileNumber());
                throw new UserException("Unable to create user", 500);
            }
            userModel = userModel1;
        }

        Integer mockCount = 0;
        EssentialDetailsModel essentialDetailsModel = EssentialDetailsModel.builder()
                .userId(userModel.getId())
                .mobileNumber(userRequestModel.getMobileNumber())
                .amount(userRequestModel.getAmount())
                .txnCount(mockCount)
                .PanOTPCount(mockCount)
                .MobOTPCount(mockCount)
                .build();
        EssentialDetailsModel savedEssentialDetailsModel = essentialDetailsRepository.save(essentialDetailsModel);
        if (savedEssentialDetailsModel == null) {
            log.error("Error while saving Essential Details with user mobile number: " + userModel.getMobileNumber());
            throw new UserException("Some error occurred please try again after some time", 500);
        }


        TrackStageModel trackStage = TrackStageModel.builder()
                .trackId(essentialDetailsModel.getDetailsId())
                .selection(TrackStageModel.selectionStage.LENDER_SELECTION)
                .build();
        TrackStageModel savedTrackStageModel = trackStageRepository.save(trackStage);
        if (savedTrackStageModel == null) {
            log.error("Error while saving Track Stage with user mobile number: " + userModel.getMobileNumber());
            throw new UserException("Some error occurred please try again after some time", 500);
        }

        UserResponseModel userResponseModel = UserResponseModel.builder()
                .detailsId(essentialDetailsModel.getDetailsId())
                .trackId(trackStage.getTrackId())
                .userId(userModel.getId())
                .mobileNumber(userRequestModel.getMobileNumber())
                .amount(userRequestModel.getAmount())
                .statusCode(HttpStatus.OK.value())
                .build();

        return userResponseModel;
    }

}
