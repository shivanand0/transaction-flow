package com.flexmoney.transactionflow.service;
import com.flexmoney.transactionflow.error.CreditLimitException;
import com.flexmoney.transactionflow.model.*;
import com.flexmoney.transactionflow.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

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
                .txnCount(0)
                .PanOTPCount(0)
                .MobOTPCount(0)
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
    public ResponseEntity<Details> getDetails(UUID detailsId) {
        boolean check = checkTxnCountValues(detailsId, "txncount");
        if(check == false){
            // thro error "You're not approved by our lenders for transaction";
            return new ResponseEntity<>(Details
                    .builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(false)
                    .message("You're not approved by our lenders for this transaction")
                    .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

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

        return new ResponseEntity<>(Details.builder()
                .statusCode(HttpStatus.OK.value())
                .status(true)
                .userName(userRepository.findById(userId).get().getUserName())
                .mobileNumber(mobileNumber)
                .amount(amount)
                .lenderDetailsList(lenderDetailsList)
                .build(), HttpStatus.OK);

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
                            msg = "PAN-OTP-EXCEED";
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
                            msg = "MOB-OTP-EXCEED";
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
        public ResponseEntity<TransactionResponse> InitTransaction(String txnType, TransactionDTO transactionDTO) {
                UUID detailsId = transactionDTO.getDetailsId();
                String status = transactionDTO.getStatus();
                String remark = transactionDTO.getRemark();

                boolean check = checkTxnCountValues(detailsId, "txncount");
                if(check == false){
                    TransactionResponse transactionResponse = new TransactionResponse();
                    transactionResponse.setStatus(false);
                    transactionResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    transactionResponse.setMessage("TIMEOUT");

                    return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
                }

                if(txnType.equals("initiate")){
                    boolean checkTxnExists = checkIfTxnExists(detailsId);
                    if(checkTxnExists == true){
                        TransactionResponse transactionResponse = new TransactionResponse();
                        transactionResponse.setStatus(false);
                        transactionResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                        transactionResponse.setMessage("Transaction already initiated");

                        return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
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
                    transaction.setStatus(status); // here status will be "initiate"
                    transactionRepository.save(transaction);

                    essentialDetailsRepository.updateStatusRemarkById(detailsId, txnType, remark);
                } else if(txnType.equals("confirm")){
                    // here status will be SUCCESS / FAIL based on OTP verification
                    boolean checkTxnExists = checkIfTxnExists(detailsId);
                    if(checkTxnExists == false){
                        TransactionResponse transactionResponse = new TransactionResponse();
                        transactionResponse.setStatus(false);
                        transactionResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                        transactionResponse.setMessage("Transaction not initiated");

                        return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
                    }

                    TransactionModel transaction = transactionRepository.findByDetailsId(detailsId);
                    UUID txnId = transaction.getTxnId();
                    transactionRepository.updateFieldsById(txnId, status);

                    essentialDetailsRepository.updateStatusRemarkById(detailsId, txnType, status+" "+remark);

                } else {
                    TransactionResponse transactionResponse = new TransactionResponse();
                    transactionResponse.setStatus(false);
                    transactionResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    transactionResponse.setMessage("URL not found");

                    return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
                }

                TransactionResponse transactionResponse = new TransactionResponse();
                transactionResponse.setStatus(true);
                transactionResponse.setStatusCode(HttpStatus.CREATED.value());
                transactionResponse.setMessage("Successful");

                return new ResponseEntity<>(transactionResponse, HttpStatus.CREATED);
        }

        public boolean checkTxnCountValues(UUID detailsId, String checkFor){
            // checkFor : txncount, panotp, mobileotp
            EssentialDetails essentialDetails = essentialDetailsRepository.findById(detailsId).get();
            boolean status=true;
            String remark="";
            Integer cnt=0;
            if(essentialDetails == null) {
                status = false;
                remark = "Invalid Transaction ID";
            } else {
                Integer txnCount = essentialDetails.getTxnCount();
                Integer PanOTPCount = essentialDetails.getPanOTPCount();
                Integer MobOTPCount = essentialDetails.getMobOTPCount();

                if(checkFor == "panotp") cnt = PanOTPCount;
                else if(checkFor == "mobileotp") cnt = MobOTPCount;

                if(cnt >= 3){
                    status = false;
                    remark = "Rate limiter hit";
                }else{
                    // Update count++
                    if(checkFor == "txncount") {
                        ++txnCount;

                        // check the created timestamp with current timestamp
                        Date d1 = essentialDetails.getCreatedAt();
                        Date d2 = new Date();

                        long difference_In_Time = d2.getTime() - d1.getTime();
                        long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;

                        if(difference_In_Minutes > 10) {
                            status = false;
                            remark = "transaction timeout";
                        }

                        status=true;
                        remark="success";
                    }
                    else if(checkFor == "panotp") {
                        ++PanOTPCount;
                        status=true;
                        remark="success";
                    }
                    else if(checkFor == "mobileotp") {
                        ++MobOTPCount;
                        status=true;
                        remark="success";
                    }

                    essentialDetailsRepository.updateFieldsById(txnCount, PanOTPCount, MobOTPCount, detailsId);
                }
            }

            return status;
        }

        public boolean checkIfTxnExists(UUID detailsId){
            boolean status=false;
            TransactionModel transactionModel = transactionRepository.findByDetailsId(detailsId);
            if(transactionModel != null) {
                // if txn entry with detailsID exists then return true
                status = true;
            }

            return status;
        }
}
