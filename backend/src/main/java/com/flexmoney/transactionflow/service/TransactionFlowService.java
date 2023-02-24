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
        private ITransactionRepository transactionRepository;

    @Autowired
    private IEssentialDetailsRepository essentialDetailsRepository;

    @Override
    public ResponseEntity<UserResponse> saveUser(UserDTO userDTO) throws CreditLimitException {

        UserModel userModel = userRepository.findByMobileNumber(userDTO.getMobileNumber());

        if (userModel == null) {
            UserModel user = new UserModel();
            user.setUserName(userDTO.getUserName());
            user.setMobileNumber(userDTO.getMobileNumber());
            user.setLastFourDigitsOfPan(1234L);

            List<LenderIdModel> lenderIdModelList = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                LenderIdModel lenderIdModel = LenderIdModel.builder().lenderId(i)
                        .creditLimit(50000.00)
                        .build();
                lenderIdModelList.add(lenderIdModel);
            }
            user.setLenderId(lenderIdModelList);
            UserModel userModel1 = userRepository.save(user);
            userModel = userModel1;
        }

        TrackStageModel trackStage = new TrackStageModel();
        trackStage.setSelection(TrackStageModel.selectionStage.LENDER_SELECTION);
        TrackStageModel savedTrackStage = trackStageRepository.save(trackStage);
        UUID trackId = savedTrackStage.getTrackId();

        EssentialDetails essentialDetails = EssentialDetails.builder()
                .trackId(trackId)
                .userId(userModel.getId())
                .mobileNumber(userDTO.getMobileNumber())
                .amount(userDTO.getAmount())
                .build();

        essentialDetailsRepository.save(essentialDetails);


        UserResponse userResponse = UserResponse.builder()
                .detailsId(essentialDetails.getDetailsId())
                .trackId(trackId)
                .userId(userModel.getId())
                .mobileNumber(userDTO.getMobileNumber())
                .amount(userDTO.getAmount())
                .statusCode(HttpStatus.OK.value())
                .build();

        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> saveTrackStage(UUID detailsId, TrackStageDTO trackStageDTO) {
        EssentialDetails essentialDetails = essentialDetailsRepository.findById(detailsId).get();
        UUID trackId = essentialDetails.getTrackId();
        TrackStageModel.selectionStage selection = trackStageDTO.getSelection();
        Integer selectedLenderId = trackStageDTO.getSelectedLenderId();
        Integer selectedLenderInfoId = trackStageDTO.getSelectedLenderInfoId();
        trackStageRepository.updateRemainingFieldsById(selection, trackId, selectedLenderId, selectedLenderInfoId);
        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    @Override
    public LenderInfoModel addLender(LenderInfoDTO lenderInfoDTO) {

        LenderModel lender = lenderRepository.findByLenderName(lenderInfoDTO.getLender().getLenderName());
        LenderInfoModel lenderInfo;
        LenderModel lenderModel;

        if (lender != null) {
            lenderModel = lender;
        } else {
            lenderModel = lenderInfoDTO.getLender();
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

        Optional<EssentialDetails> detailsDTO = essentialDetailsRepository.findById(detailsId);
        Long userId = detailsDTO.get().getUserId();
        double amount = detailsDTO.get().getAmount();
        String mobileNumber = detailsDTO.get().getMobileNumber();

        List<LenderIdModel> lenderIdModelList = lenderIdRepository.findAllByUserMobileNumber(mobileNumber);
        List<Integer> userPreApprovedLenders = new ArrayList<>();
        lenderIdModelList.forEach(lender -> userPreApprovedLenders.add(lender.getLenderId()));


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
                if (rate != 0) {
                    emi = (int) Math.ceil((principal * rate * Math.pow(1 + rate, time)) / (Math.pow(1 + rate, time) - 1));
                    totalInterest = (emi * time) - principal;
                } else {
                    emi = (int) Math.ceil(principal / time);
                    totalInterest = 0;
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
                    .primaryLogoUrl(lenderRepository.findById(lenderID).get().getPrimaryLogoUrl())
                    .secondaryLogoUrl(lenderRepository.findById(lenderID).get().getSecondaryLogoUrl())
                    .lenderType("EMI")
                    .emiDetailsList(emiDetailsList)
                    .build();

            lenderDetailsList.add(lenderDetails);
        }
        return Details.builder()
                .statusCode(HttpStatus.OK.value())
                .userName(userRepository.findById(userId).get().getUserName())
                .mobileNumber(mobileNumber)
                .amount(amount)
                .lenderDetailsList(lenderDetailsList)
                .build();
    }


    @Override
    public ResponseEntity<TwoFVerificationResponse> OtpVerification(String verificationType, TwoFVerificationDTO twoFVerificationDTO) {
                UUID detailsId = twoFVerificationDTO.getDetailsId();
                long receivedOtp = twoFVerificationDTO.getReceivedOtp();
                boolean status;
                long expectedMobileOtp = 1234;
                String msg;
                HttpStatus statusCode;

                EssentialDetails essentialDetails = essentialDetailsRepository.findById(detailsId).get();
                long userId = essentialDetails.getUserId();
                UserModel userModel = userRepository.findById(userId).get();

                if(verificationType.equals("PAN")){
                        boolean check = checkTxnCountValues(detailsId, "panotp");
                        if(check == false){
                            msg = "You're not approved by our lenders for transaction";
                            statusCode = HttpStatus.BAD_REQUEST;
                            status=false;
                        }
                        else {
                            long lastFourDigitsOfPan = userModel.getLastFourDigitsOfPan();

                            if (receivedOtp == lastFourDigitsOfPan) {
                                msg = "PAN Verification Successfull";
                                statusCode = HttpStatus.ACCEPTED;
                                status = true;
                            } else {
                                msg = "PAN Verification Failed";
                                statusCode = HttpStatus.EXPECTATION_FAILED;
                                status = false;
                            }
                        }
                } else if(verificationType.equals("MOBILE")){
                        boolean check = checkTxnCountValues(detailsId, "mobileotp");
                        if(check == false){
                            msg = "You're not approved by our lenders for transaction";
                            statusCode = HttpStatus.BAD_REQUEST;
                            status=false;
                        }
                        else {
                            if (receivedOtp == expectedMobileOtp) {
                                msg = "Mobile Verification Successfull";
                                statusCode = HttpStatus.ACCEPTED;
                                status = true;
                            } else {
                                msg = "Mobile Verification Failed";
                                statusCode = HttpStatus.EXPECTATION_FAILED;
                                status = false;
                            }
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
                UUID detailsId = transactionDTO.getDetailsId();
                String status = transactionDTO.getStatus();

                boolean check = checkTxnCountValues(detailsId, "txncount");
                if(check == false){
                    TransactionResponse transactionResponse = new TransactionResponse();
                    transactionResponse.setStatus(false);
                    transactionResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    transactionResponse.setMessage("You're not approved by our lenders for transaction");

                    return new ResponseEntity<>(transactionResponse, HttpStatus.CREATED);
                }

                EssentialDetails essentialDetails = essentialDetailsRepository.findById(detailsId).get();
                long userId = essentialDetails.getUserId();
                UUID trackId = essentialDetails.getTrackId();

                TrackStageModel trackStageModel = trackStageRepository.findByTrackId(trackId);

                Integer lenderInfoId = trackStageModel.getSelectedLenderInfoId();

                TransactionModel transaction = new TransactionModel();
                transaction.setDetailsId(detailsId);
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

        public boolean checkTxnCountValues(UUID detailsId, String checkFor){
            // checkFor : txncount, panotp, mobileotp
            EssentialDetails essentialDetails = essentialDetailsRepository.findById(detailsId).get();
            boolean val=false;
            Integer cnt=0;
            if(essentialDetails == null) {
                val = false;
            } else {
                Integer txnCount = essentialDetails.getTxnCount();
                Integer PanOTPCount = essentialDetails.getPanOTPCount();
                Integer MobOTPCount = essentialDetails.getMobOTPCount();

                if(checkFor == "txncount") cnt = txnCount;
                else if(checkFor == "panotp") cnt = PanOTPCount;
                else if(checkFor == "mobileotp") cnt = MobOTPCount;

                if(cnt >= 3){
                    val = false;
                }else{
                    val = true;
                    // Update count++
                    if(checkFor == "txncount") ++txnCount;
                    else if(checkFor == "panotp") ++PanOTPCount;
                    else if(checkFor == "mobileotp") ++MobOTPCount;

                    essentialDetailsRepository.updateFieldsById(txnCount, PanOTPCount, MobOTPCount, detailsId);
                }
            }

            return val;
        }
}
