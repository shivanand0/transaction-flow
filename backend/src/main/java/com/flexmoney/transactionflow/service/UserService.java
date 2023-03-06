package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.*;
import com.flexmoney.transactionflow.repository.IEssentialDetailsRepository;
import com.flexmoney.transactionflow.repository.ILenderRepository;
import com.flexmoney.transactionflow.repository.ITrackStageRepository;
import com.flexmoney.transactionflow.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService{

    private static final Pattern regexp = Pattern.compile("^[6-9][0-9]{9}$");
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IEssentialDetailsRepository essentialDetailsRepository;

    @Autowired
    private ITrackStageRepository trackStageRepository;

    @Autowired
    private ILenderRepository lenderRepository;

    @Override
    public ResponseEntity<UserResponseModel> saveUser(UserRequestModel userRequestModel) throws Exception {

        Matcher matcher =  regexp.matcher(userRequestModel.getMobileNumber());
        if (!matcher.find()) {
            throw new Exception("Please enter a valid mobile number");
        }
        UserModel userModel = userRepository.findByMobileNumber(userRequestModel.getMobileNumber());

        if (userModel == null) {
            Long mockLastFourDigitsOfPan=1234L;
            double mockCreditLimit=50000.00;

            UserModel user = new UserModel();
            user.setUserName(userRequestModel.getUserName());
            user.setMobileNumber(userRequestModel.getMobileNumber());
            user.setLastFourDigitsOfPan(mockLastFourDigitsOfPan);

            List<LenderModel> lenderModelList= lenderRepository.findAll();
            List<Integer> allLenderIds =lenderModelList.stream()
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
            userModel = userModel1;
        }

        Integer mockCount=0;
        EssentialDetailsModel essentialDetailsModel = EssentialDetailsModel.builder()
                .userId(userModel.getId())
                .mobileNumber(userRequestModel.getMobileNumber())
                .amount(userRequestModel.getAmount())
                .txnCount(mockCount)
                .PanOTPCount(mockCount)
                .MobOTPCount(mockCount)
                .build();
        essentialDetailsRepository.save(essentialDetailsModel);


        TrackStageModel trackStage = TrackStageModel.builder()
                .selection(TrackStageModel.selectionStage.LENDER_SELECTION)
                .trackId(essentialDetailsModel.getDetailsId())
                .build();
        TrackStageModel savedTrackStage = trackStageRepository.save(trackStage);
        UUID trackId = savedTrackStage.getTrackId();


        UserResponseModel userResponseModel = UserResponseModel.builder()
                .detailsId(essentialDetailsModel.getDetailsId())
                .trackId(trackId)
                .userId(userModel.getId())
                .mobileNumber(userRequestModel.getMobileNumber())
                .amount(userRequestModel.getAmount())
                .statusCode(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(userResponseModel, HttpStatus.CREATED);
    }


}
