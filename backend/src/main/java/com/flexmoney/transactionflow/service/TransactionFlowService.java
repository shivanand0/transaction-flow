package com.flexmoney.transactionflow.service;
import com.flexmoney.transactionflow.model.*;
import com.flexmoney.transactionflow.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionFlowService implements ITransactionFlowService {
        @Autowired
        private IUserRepository userRepository;
        @Autowired
        private ITrackStageRepository trackStageRepository;

        @Autowired
        private ILenderInfoRepository lenderInfoRepository;

        @Autowired
        private ILenderRepository lenderRepository;
        @Autowired
        private ILenderIdRepository lenderIdRepository;

        @Override
        public ResponseEntity<UserResponse> saveUser(UserDTO userDTO){
                UserModel user = new UserModel();
                user.setUserName(userDTO.getUserName());
                user.setMobileNumber(userDTO.getMobileNumber());
                user.setCreditLimit(50000.00);
                user.setLastFourDigitsOfPan(1234L);
                List<LenderIdModel> lenderIdModelList= new ArrayList<>();
                for (int i = 1; i <= 3; i++) {
                        LenderIdModel lenderIdModel= LenderIdModel.builder().lenderId(i).build();
                        lenderIdModelList.add(lenderIdModel);
                }
                user.setLenderId(lenderIdModelList);
                UserModel savedUser=userRepository.save(user);
                TrackStageModel trackStage = new TrackStageModel();
                Long userId=savedUser.getId();
                trackStage.setUserId(userId);
                trackStage.setAmount(userDTO.getAmount());
                TrackStageModel savedTrackStage=trackStageRepository.save(trackStage);
                Long trackId=savedTrackStage.getTrackId();
                //return userId, trackId, status-code
                UserResponse userResponse =new UserResponse();
                userResponse.setUserId(userId);
                userResponse.setTrackId(trackId);
                userResponse.setStatusCode(HttpStatus.CREATED.value());
                return new ResponseEntity<>(userResponse,HttpStatus.CREATED);
        }

        @Override
        public ResponseEntity<?> saveTrackStage(Long trackId,TrackStageDTO trackStageDTO) {
                TrackStageModel.selectionStage selection = trackStageDTO.getSelection();
                Integer selectedLenderId = trackStageDTO.getSelectedLenderId();
                Integer selectedTenureId = trackStageDTO.getSelectedTenureId();
                trackStageRepository.updateRemainingFieldsById(selection,trackId,selectedLenderId,selectedTenureId);
                return new ResponseEntity<>("Success",HttpStatus.CREATED);
        }
        //fetch user details from getDetails() api
        @Override
        public LenderInfoModel addLender(LenderInfoDTO lenderInfoDTO){

                LenderModel lender=lenderRepository.findByLenderName(lenderInfoDTO.getLender().getLenderName());
                LenderInfoModel lenderInfo;
                if(lender!=null){
                        lenderInfo = LenderInfoModel.builder()
                                .lender(lender)
                                .tenure(lenderInfoDTO.getTenure())
                                .tenureType(lenderInfoDTO.getTenureType())
                                .rateOfInterest(lenderInfoDTO.getRateOfInterest())
                                .build();
                }
                else {
                        lenderInfo = LenderInfoModel.builder()
                                .lender(lenderInfoDTO.getLender())
                                .tenure(lenderInfoDTO.getTenure())
                                .tenureType(lenderInfoDTO.getTenureType())
                                .rateOfInterest(lenderInfoDTO.getRateOfInterest())
                                .build();
                }
                return lenderInfoRepository.save(lenderInfo);
        }
        @Override
        public Details getDetails() {
                List<LenderIdModel> lenderIdModelList= lenderIdRepository.findAllByUserFid(2);
                List<Integer> userPreApprovedLenders = new ArrayList<>();
                lenderIdModelList.forEach(lender->userPreApprovedLenders.add(lender.getLenderId()));
                double amount = 10000;

                List<LenderDetails> lenderDetailsList = new ArrayList<>();


                for (Integer lenderID : userPreApprovedLenders) {

                        List<LenderInfoModel> lenderInfo = lenderInfoRepository.findAllByLenderId(lenderID);
                        List<EmiDetails> emiDetailsList = new ArrayList<>();

                        for (LenderInfoModel lenderI : lenderInfo) {
                                double principal = amount;
                                double rate = lenderI.getRateOfInterest() / (12 * 100);
                                Integer time = lenderI.getTenure();
                                Integer emi;
                                double totalInterest;
                                if(rate!=0){
                                        emi = (int) Math.ceil((principal * rate * Math.pow(1 + rate, time)) / (Math.pow(1 + rate, time) - 1));
                                        totalInterest=(emi*time) - principal;
                                }
                                else{
                                        emi= (int) Math.ceil(principal/time);
                                        totalInterest=0;
                                }

                                EmiDetails emiDetails = EmiDetails.builder()
                                        .loanDuration(time)
                                        .interestRate(lenderI.getRateOfInterest())
                                        .monthlyInstallment(emi)
                                        .totalInterest(totalInterest)
                                        .loanAmount(principal)
                                        .tenureType(lenderI.getTenureType())
                                        .build();
                                emiDetailsList.add(emiDetails);
                        }
                        LenderDetails lenderDetails = LenderDetails.builder()
                                .lenderId(lenderID)
                                .lenderName(lenderRepository.findById(lenderID).get().getLenderName())
                                .lenderType("EMI")
                                .emiDetailsList(emiDetailsList)
                                .build();

                        lenderDetailsList.add(lenderDetails);
                }
                return Details.builder()
                        .lenderDetailsList(lenderDetailsList)
                        .build();
        }

}
