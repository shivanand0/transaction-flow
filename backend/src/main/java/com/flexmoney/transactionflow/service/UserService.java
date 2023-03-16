package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.core.error.UserException;
import com.flexmoney.transactionflow.error.ApiResponseCodeEnum;
import com.flexmoney.transactionflow.error.ErrorDetails;
import com.flexmoney.transactionflow.model.*;
import com.flexmoney.transactionflow.repository.IUserVerificationInfoRepository;
import com.flexmoney.transactionflow.repository.ITrackStageRepository;
import com.flexmoney.transactionflow.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class UserService implements IUserService{
    private static final Pattern regexp = Pattern.compile("^[6-9][0-9]{9}$");
    @Autowired
    private IUserVerificationInfoRepository essentialDetailsRepository;
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ITrackStageRepository trackStageRepository;
    @Override
    public ResponseEntity<UserResponse> saveUser(UserDTO userDTO) throws Exception{

        Matcher matcher =  regexp.matcher(userDTO.getMobileNumber());
        if (!matcher.find()) {
            throw new UserException(ApiResponseCodeEnum.INVALID_MOBILE_NUMBER.getCode(),"Invalid mobile number","Invalid mobile number", HttpStatus.BAD_REQUEST);
        }
        EUser EUser = userRepository.findByMobileNumber(userDTO.getMobileNumber());

        if (EUser == null) {
            EUser user = new EUser();
            user.setUserName(userDTO.getUserName());
            user.setMobileNumber(userDTO.getMobileNumber());
            user.setLastFourDigitsOfPan(1234L);

            List<ELenderId> ELenderIdList = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                ELenderId ELenderId = com.flexmoney.transactionflow.model.ELenderId.builder().lenderId(i)
                        .creditLimit(50000.00)
                        .build();
                ELenderIdList.add(ELenderId);
            }
            user.setLenderId(ELenderIdList);
            EUser EUser1 = userRepository.save(user);
            EUser = EUser1;
        }

        ETrackStage trackStage = new ETrackStage();
        trackStage.setSelection(ETrackStage.selectionStage.LENDER_SELECTION);
        ETrackStage savedTrackStage = trackStageRepository.save(trackStage);
        UUID trackId = savedTrackStage.getTrackId();

        EUserVerificationInfo EUserVerificationInfo = com.flexmoney.transactionflow.model.EUserVerificationInfo.builder()
                .trackId(trackId)
                .userId(EUser.getId())
                .mobileNumber(userDTO.getMobileNumber())
                .amount(userDTO.getAmount())
                .txnCount(0)
                .PanOTPCount(0)
                .MobOTPCount(0)
                .build();

        essentialDetailsRepository.save(EUserVerificationInfo);


        UserResponse userResponse = UserResponse.builder()
                .detailsId(EUserVerificationInfo.getDetailsId())
                .trackId(trackId)
                .userId(EUser.getId())
                .mobileNumber(userDTO.getMobileNumber())
                .amount(userDTO.getAmount())
                .statusCode(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }
    // User Exception handler
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorDetails> handleException(UserException e) {
        ErrorDetails errorDetailsResponse = new ErrorDetails(e.getCode(),e.getTitle(),e.getMessage(),e.getStatus().value());
        return new ResponseEntity<>(errorDetailsResponse, e.getStatus());
    }
}
