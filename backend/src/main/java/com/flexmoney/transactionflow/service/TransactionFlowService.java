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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TransactionFlowService implements ITransactionFlowService {
        private static final Pattern regexp = Pattern.compile("^[6-9][0-9]{9}$");
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
    public ResponseEntity<UserResponse> saveUser(UserDTO userDTO) throws Exception {

        Matcher matcher =  regexp.matcher(userDTO.getMobileNumber());
        if (!matcher.find()) {
           throw new Exception("Please enter a valid mobile number");
        }
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
                    .message("Transaction Timeout")
                    .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        boolean check2 = checkIfTxnIsCompleted(detailsId);
        if(check2 == true){
            return new ResponseEntity<>(Details
                    .builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(false)
                    .message("This transaction is completed!")
                    .build(),
                    HttpStatus.BAD_REQUEST
            );
        };

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
    public ResponseEntity<TransactionResponse> InitiateTxn(TransactionDTO transactionDTO) {
        UUID detailsId = transactionDTO.getDetailsId();
        Long receivedOtp = transactionDTO.getOtp();
        String statusStr = transactionDTO.getStatus();
        String remark = transactionDTO.getRemark();

        boolean status;
        String msg;
        HttpStatus statusCode;

        boolean check2 = checkIfTxnIsCompleted(detailsId);
        if (check2 == true) {
            msg = "Transaction Completed Already!";
            statusCode = HttpStatus.BAD_REQUEST;
            status=false;
        }
        else {
            boolean check = checkTxnCountValues(detailsId, "panotp");
            if (!check) {
                // either block user for 24 hours
                // or mark txn as fail
                essentialDetailsRepository.updateStatusRemarkById(detailsId, "FAIL", "PAN-OTP-EXCEED");

                msg = "PAN-OTP-EXCEED";
                statusCode = HttpStatus.BAD_REQUEST;
                status = false;
            } else {
                EssentialDetails essentialDetails = essentialDetailsRepository.findById(detailsId).get();
                long userId = essentialDetails.getUserId();
                UserModel userModel = userRepository.findById(userId).get();
                long lastFourDigitsOfPan = userModel.getLastFourDigitsOfPan();

                if (receivedOtp == lastFourDigitsOfPan) {
                    // Initiate the txn
                    UUID trackId = essentialDetails.getTrackId();
                    TrackStageModel trackStageModel = trackStageRepository.findByTrackId(trackId);
                    Integer lenderInfoId = trackStageModel.getSelectedLenderInfoId();

                    boolean checkTxnExists = checkIfTxnExists(detailsId);
                    if (checkTxnExists) {
                        // here update the txn details
                        transactionRepository.updateLenderInfoIdFieldByDetailsId(detailsId, lenderInfoId);
                        essentialDetailsRepository.updateStatusRemarkById(detailsId, "initiate", remark);
                    } else {
                        // here add new entry
                        TransactionModel transaction = new TransactionModel();
                        transaction.setDetailsId(detailsId);
                        transaction.setUserId(userId);
                        transaction.setLenderInfoId(lenderInfoId);
                        transaction.setStatus(statusStr.toUpperCase()); // here status will be "initiate"
                        transactionRepository.save(transaction);

                        essentialDetailsRepository.updateStatusRemarkById(detailsId, "initiate", remark);
                    }

                    msg = "OTP Sent Successful";
                    statusCode = HttpStatus.CREATED;
                    status = true;
                } else {
                    msg = "Invalid PAN Details";
                    statusCode = HttpStatus.BAD_REQUEST;
                    status = false;
                }
            }
        }


        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setStatus(status);
        transactionResponse.setStatusCode(statusCode.value());
        transactionResponse.setMessage(msg);

        return new ResponseEntity<>(transactionResponse, statusCode);
    }

    @Override
    public ResponseEntity<TransactionResponse> ConfirmTxn(TransactionDTO transactionDTO) {
        UUID detailsId = transactionDTO.getDetailsId();
        Long receivedOtp = transactionDTO.getOtp();
        String statusStr = transactionDTO.getStatus();
        String remark = transactionDTO.getRemark();
        long expectedMobileOtp = 1234;

        boolean status;
        String msg;
        HttpStatus statusCode;

        TransactionModel transaction = transactionRepository.findByDetailsId(detailsId);
        UUID txnId = transaction.getTxnId();

        boolean check = checkTxnCountValues(detailsId, "mobileotp");
        if (!check) {
            // either block user for 24 hours
            // or mark txn as fail
            transactionRepository.updateFieldsById(txnId, "FAIL");
            essentialDetailsRepository.updateStatusRemarkById(detailsId, "FAIL", "MOB-OTP-EXCEED");
            msg = "MOB-OTP-EXCEED";
            statusCode = HttpStatus.BAD_REQUEST;
            status=false;
        }
        else {
            if (receivedOtp == expectedMobileOtp) {

                transactionRepository.updateFieldsById(txnId, "SUCCESS");

                msg = "Success";
                statusCode = HttpStatus.ACCEPTED;
                status = true;

                essentialDetailsRepository.updateStatusRemarkById(detailsId, "SUCCESS", status+" "+remark);
            } else {
                msg = "Invalid OTP";
                statusCode = HttpStatus.BAD_REQUEST;
                status = false;
            }
        }

        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setStatus(status);
        transactionResponse.setStatusCode(statusCode.value());
        transactionResponse.setMessage(msg);

        return new ResponseEntity<>(transactionResponse, statusCode);
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
                        } else {
                            status=true;
                            remark="success";
                        }
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

        public boolean checkIfTxnIsCompleted(UUID detailsId) {
            boolean status = false;
            TransactionModel transactionModel = transactionRepository.findByDetailsId(detailsId);
            if(transactionModel != null) {
                String txnStatus = transactionModel.getStatus();
                if (txnStatus.equalsIgnoreCase("SUCCESS") || txnStatus.equalsIgnoreCase("FAIL")) {
                    status = true;
                }
            }

            EssentialDetails essentialDetails = essentialDetailsRepository.findById(detailsId).get();
            String detailsStatus = essentialDetails.getStatus();
            if(detailsStatus != null) {
                if (detailsStatus.equalsIgnoreCase("SUCCESS") || detailsStatus.equalsIgnoreCase("FAIL")) {
                    status = true;
                }
            }
            return status;
        }
}
