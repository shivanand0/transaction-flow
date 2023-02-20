package com.flexmoney.transactionflow.service;
import com.flexmoney.transactionflow.error.CreditLimitException;
import com.flexmoney.transactionflow.model.*;
import com.flexmoney.transactionflow.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        @Autowired
        private IEssentialDetailsRepository essentialDetailsRepository;

        @Override
        public ResponseEntity<UserResponse> saveUser(UserDTO userDTO) throws CreditLimitException {

                UserModel userModel=userRepository.findByMobileNumber(userDTO.getMobileNumber());

                if(userModel==null){
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
                        UserModel userModel1=userRepository.save(user);
                        userModel= userModel1;
                }

                if(userDTO.getAmount()>50000){
                        throw new CreditLimitException("Transaction amount greater than your Credit Limit");
                }

                EssentialDetails checkEssentialDetails=essentialDetailsRepository.findByMobileNumber(userDTO.getMobileNumber());
                EssentialDetails essentialDetails;

                if (checkEssentialDetails==null){
                        essentialDetails = EssentialDetails.builder()
                                .userId(userModel.getId())
                                .mobileNumber(userDTO.getMobileNumber())
                                .amount(userDTO.getAmount())
                                .build();
                        essentialDetailsRepository.save(essentialDetails);
                }else{
                        checkEssentialDetails.setAmount(userDTO.getAmount());
                        essentialDetails=checkEssentialDetails;
                }

                TrackStageModel trackStage = new TrackStageModel();
                trackStage.setSelection(TrackStageModel.selectionStage.LENDER_SELECTION);
                TrackStageModel savedTrackStage=trackStageRepository.save(trackStage);
                UUID trackId=savedTrackStage.getTrackId();


                UserResponse userResponse=UserResponse.builder()
                        .detailsId(essentialDetails.getDetailsId())
                        .trackId(trackId)
                        .userId(userModel.getId())
                        .mobileNumber(userDTO.getMobileNumber())
                        .amount(userDTO.getAmount())
                        .statusCode(HttpStatus.OK.value())
                        .build();

                return new ResponseEntity<>(userResponse,HttpStatus.CREATED);
        }

        @Override
        public ResponseEntity<?> saveTrackStage(UUID trackId,TrackStageDTO trackStageDTO) {
                TrackStageModel.selectionStage selection = trackStageDTO.getSelection();
                Integer selectedLenderId = trackStageDTO.getSelectedLenderId();
                Integer selectedTenureId = trackStageDTO.getSelectedTenureId();
                trackStageRepository.updateRemainingFieldsById(selection,trackId,selectedLenderId,selectedTenureId);
                return new ResponseEntity<>("Success",HttpStatus.CREATED);
        }

        @Override
        public LenderInfoModel addLender(LenderInfoDTO lenderInfoDTO){

                LenderModel lender=lenderRepository.findByLenderName(lenderInfoDTO.getLender().getLenderName());
                LenderInfoModel lenderInfo;
                LenderModel lenderModel;

                if(lender!=null){
                       lenderModel=lender;
                }
                else {
                        lenderModel=lenderInfoDTO.getLender();
                }

                lenderInfo = LenderInfoModel.builder()
                        .lender(lenderModel)
                        .tenure(lenderInfoDTO.getTenure())
                        .tenureType(lenderInfoDTO.getTenureType())
                        .rateOfInterest(lenderInfoDTO.getRateOfInterest())
                        .build();
                return lenderInfoRepository.save(lenderInfo);
        }
        @Override
        public Details getDetails(UUID detailsId) {
                Optional<EssentialDetails> detailsDTO= essentialDetailsRepository.findById(detailsId);
                Long userId=detailsDTO.get().getUserId();
                double amount=detailsDTO.get().getAmount();
                String mobileNumber=detailsDTO.get().getMobileNumber();

                List<LenderIdModel> lenderIdModelList= lenderIdRepository.findAllByUserMobileNumber(mobileNumber);
                List<Integer> userPreApprovedLenders = new ArrayList<>();
                lenderIdModelList.forEach(lender->userPreApprovedLenders.add(lender.getLenderId()));


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
                        .userName(userRepository.findById(userId).get().getUserName())
                        .mobileNumber(mobileNumber)
                        .amount(amount)
                        .lenderDetailsList(lenderDetailsList)
                        .build();
        }

}
