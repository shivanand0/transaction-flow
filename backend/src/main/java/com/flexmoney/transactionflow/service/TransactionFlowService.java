package com.flexmoney.transactionflow.service;
import com.flexmoney.transactionflow.model.*;
import com.flexmoney.transactionflow.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        private ITransactionRepository transactionRepository;

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
                trackStage.setSelection(TrackStageModel.selectionStage.LENDER_SELECTION);
                TrackStageModel savedTrackStage=trackStageRepository.save(trackStage);
                UUID trackId=savedTrackStage.getTrackId();
                //return userId, trackId, status-code
                UserResponse userResponse =new UserResponse();
                userResponse.setUserId(userId);
                userResponse.setTrackId(trackId);
                userResponse.setStatusCode(HttpStatus.CREATED.value());
                return new ResponseEntity<>(userResponse,HttpStatus.CREATED);
        }

        @Override
        public ResponseEntity<?> saveTrackStage(UUID trackId,TrackStageDTO trackStageDTO) {
                TrackStageModel.selectionStage selection = trackStageDTO.getSelection();
                Integer selectedLenderId = trackStageDTO.getSelectedLenderId();
                Integer selectedLenderInfoId = trackStageDTO.getSelectedLenderInfoId();
                trackStageRepository.updateRemainingFieldsById(selection,trackId,selectedLenderId,selectedLenderInfoId);
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
        public Details getDetails(DetailsDTO detailsDTO) {
                Long userId=detailsDTO.getUserId();
                List<LenderIdModel> lenderIdModelList= lenderIdRepository.findAllByUserFid(Math.toIntExact(userId));
                List<Integer> userPreApprovedLenders = new ArrayList<>();
                lenderIdModelList.forEach(lender->userPreApprovedLenders.add(lender.getLenderId()));

                TrackStageModel trackStageModel = trackStageRepository.findByUserId(userId);
                double amount=trackStageModel.getAmount();

                List<LenderDetails> lenderDetailsList = new ArrayList<>();


                for (Integer lenderID : userPreApprovedLenders) {

                        List<LenderInfoModel> lenderInfo = lenderInfoRepository.findAllByLenderId(lenderID);
                        List<EmiDetails> emiDetailsList = new ArrayList<>();

                        for (LenderInfoModel lenderI : lenderInfo) {
                                double principal = amount;
                                double rate = lenderI.getRateOfInterest() / (12 * 100);
                                Integer time = lenderI.getTenure();
                                Integer emi;
                                Integer lenderInfoId = lenderI.getId();
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
                                        .lenderInfoId(lenderInfoId)
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
                        .mobileNumber(userRepository.findById(userId).get().getMobileNumber())
                        .amount(amount)
                        .lenderDetailsList(lenderDetailsList)
                        .build();
        }


    @Override
    public ResponseEntity<TwoFVerificationResponse> OtpVerifivation(String verificationType, TwoFVerificationDTO twoFVerificationDTO) {
                UUID trackId = twoFVerificationDTO.getTrackId();
                long receivedOtp = twoFVerificationDTO.getReceivedOtp();
                boolean status;
                long expectedMobileOtp = 1234;
                String msg;
                HttpStatus statusCode;

                TrackStageModel trackStageModel = trackStageRepository.findByTrackId(trackId);
                long userId = trackStageModel.getUserId();
                UserModel userModel = userRepository.findById(userId).get();

                double txnAmnt = trackStageModel.getAmount();
                double creditLimit = userModel.getCreditLimit();
                int comp = Double.compare(txnAmnt, creditLimit);
                if(comp == -1){
                        msg = "Transaction Amount is greater than Credit Limit";
                        statusCode = HttpStatus.ACCEPTED;
                        status=false;
                }
                else if(verificationType.equals("PAN")){
                        long lastFourDigitsOfPan = userModel.getLastFourDigitsOfPan();

                        if(receivedOtp == lastFourDigitsOfPan){
                                msg = "PAN Verification Successfull";
                                statusCode = HttpStatus.ACCEPTED;
                                status=true;
                        } else{
                                msg = "PAN Verification Failed";
                                statusCode = HttpStatus.EXPECTATION_FAILED;
                                status=false;
                        }
                } else if(verificationType.equals("MOBILE")){
                        if(receivedOtp == expectedMobileOtp){
                                msg = "Mobile Verification Successfull";
                                statusCode = HttpStatus.ACCEPTED;
                                status=true;
                        } else{
                                msg = "Mobile Verification Failed";
                                statusCode = HttpStatus.EXPECTATION_FAILED;
                                status=false;
                        }
                } else{
                        msg = "Bad Request";
                        statusCode = HttpStatus.BAD_REQUEST;
                        status=false;
                }
                TwoFVerificationResponse twoFVerificationResponse = new TwoFVerificationResponse();
                twoFVerificationResponse.setStatus(status);
                twoFVerificationResponse.setStatusCode(statusCode.value());
                twoFVerificationResponse.setMessage(msg);

                return new ResponseEntity<>(twoFVerificationResponse, statusCode);
    }

        @Override
        public ResponseEntity<TransactionResponse> InitTransaction(TransactionDTO transactionDTO) {
                UUID trackId = transactionDTO.getTrackId();
                String status = transactionDTO.getStatus();

                TrackStageModel trackStageModel = trackStageRepository.findByTrackId(trackId);
                long userId = trackStageModel.getUserId();

                Integer lenderInfoId = trackStageModel.getSelectedLenderInfoId(); // storing lenderInfoId in selectedTenureId field, selectedTenureId to be renamed to lenderInfoId

                TransactionModel transaction = new TransactionModel();
                transaction.setTrackId(trackId);
                transaction.setUserId(userId);
                transaction.setLenderInfoId(lenderInfoId);
                transaction.setStatus(status);
                transactionRepository.save(transaction);

                TransactionResponse transactionResponse = new TransactionResponse();
                transactionResponse.setStatus(true);
                transactionResponse.setStatusCode(HttpStatus.CREATED.value());
                transactionResponse.setMessage("Successful");

                return new ResponseEntity<>(transactionResponse, HttpStatus.CREATED);
        }

}
