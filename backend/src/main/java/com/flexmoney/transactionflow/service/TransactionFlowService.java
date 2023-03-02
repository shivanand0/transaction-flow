package com.flexmoney.transactionflow.service;
import com.flexmoney.transactionflow.model.*;
import com.flexmoney.transactionflow.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

        TrackStageModel trackStage = new TrackStageModel();
        trackStage.setSelection(TrackStageModel.selectionStage.LENDER_SELECTION);
        TrackStageModel savedTrackStage = trackStageRepository.save(trackStage);
        UUID trackId = savedTrackStage.getTrackId();

        EssentialDetailsModel essentialDetailsModel = EssentialDetailsModel.builder()
                .trackId(trackId)
                .userId(userModel.getId())
                .mobileNumber(userRequestModel.getMobileNumber())
                .amount(userRequestModel.getAmount())
                .txnCount(0)
                .PanOTPCount(0)
                .MobOTPCount(0)
                .build();

        essentialDetailsRepository.save(essentialDetailsModel);


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

    @Override
    public ResponseEntity<?> saveTrackStage(UUID detailsId, TrackStageRequestModel trackStageRequestModel) {
        EssentialDetailsModel essentialDetailsModel = essentialDetailsRepository.findById(detailsId).get();
        UUID trackId = essentialDetailsModel.getTrackId();
        TrackStageModel.selectionStage selection = trackStageRequestModel.getSelection();
        Integer selectedLenderId = trackStageRequestModel.getSelectedLenderId();
        Integer selectedLenderInfoId = trackStageRequestModel.getSelectedLenderInfoId();
        trackStageRepository.updateRemainingFieldsById(selection, trackId, selectedLenderId, selectedLenderInfoId);
        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    @Override
    public LenderInfoModel addLender(LenderInfoRequestModel lenderInfoRequestModel) {

        LenderModel lender = lenderRepository.findByLenderName(lenderInfoRequestModel.getLender().getLenderName());
        LenderInfoModel lenderInfo;
        LenderModel lenderModel;

        if (lender != null) {
            lenderModel = lender;
        } else {
            lenderModel = lenderInfoRequestModel.getLender();
        }

        lenderInfo = LenderInfoModel.builder()
                .lender(lenderModel)
                .tenure(lenderInfoRequestModel.getTenure())
                .tenureType(lenderInfoRequestModel.getTenureType())
                .rateOfInterest(lenderInfoRequestModel.getRateOfInterest())
                .build();
        return lenderInfoRepository.save(lenderInfo);
    }

    @Override
    public ResponseEntity<DetailsModel> getDetails(UUID detailsId) {
        boolean check = checkTxnCountValues(detailsId, "txncount");
        if(check == false){
            // thro error "You're not approved by our lenders for transaction";
            return new ResponseEntity<>(DetailsModel
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
            return new ResponseEntity<>(DetailsModel
                    .builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(false)
                    .message("This transaction is completed!")
                    .build(),
                    HttpStatus.BAD_REQUEST
            );
        };

        Optional<EssentialDetailsModel> detailsDTO = essentialDetailsRepository.findById(detailsId);
        Long userId = detailsDTO.get().getUserId();
        double amount = detailsDTO.get().getAmount();
        String mobileNumber = detailsDTO.get().getMobileNumber();

        List<LenderIdModel> lenderIdModelList = lenderIdRepository.findAllByUserMobileNumber(mobileNumber);
        List<Integer> userPreApprovedLenders = new ArrayList<>();
        lenderIdModelList.forEach(lender -> userPreApprovedLenders.add(lender.getLenderId()));


        List<LenderDetailsModel> lenderDetailsModelList = new ArrayList<>();


        for (Integer lenderID : userPreApprovedLenders) {

            List<LenderInfoModel> lenderInfo = lenderInfoRepository.findAllByLenderId(lenderID);
            List<EmiDetailsModel> emiDetailsModelList = new ArrayList<>();

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

                EmiDetailsModel emiDetailsModel = EmiDetailsModel.builder()
                        .lenderInfoId(lenderInfoId)
                        .loanDuration(time)
                        .interestRate(lenderI.getRateOfInterest())
                        .monthlyInstallment(emi)
                        .totalInterest(totalInterest)
                        .loanAmount(principal)
                        .tenureType(lenderI.getTenureType())
                        .build();
                emiDetailsModelList.add(emiDetailsModel);
            }
            LenderDetailsModel lenderDetailsModel = LenderDetailsModel.builder()
                    .lenderId(lenderID)
                    .lenderName(lenderRepository.findById(lenderID).get().getLenderName())
                    .primaryLogoUrl(lenderRepository.findById(lenderID).get().getPrimaryLogoUrl())
                    .secondaryLogoUrl(lenderRepository.findById(lenderID).get().getSecondaryLogoUrl())
                    .lenderType("EMI")
                    .emiDetailsModelList(emiDetailsModelList)
                    .build();

            lenderDetailsModelList.add(lenderDetailsModel);
        }

        return new ResponseEntity<>(DetailsModel.builder()
                .statusCode(HttpStatus.OK.value())
                .status(true)
                .userName(userRepository.findById(userId).get().getUserName())
                .mobileNumber(mobileNumber)
                .amount(amount)
                .lenderDetailsModelList(lenderDetailsModelList)
                .build(), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<TransactionResponse> InitiateTxn(TransactionRequestModel transactionRequestModel) {
        UUID detailsId = transactionRequestModel.getDetailsId();
        Long receivedOtp = transactionRequestModel.getOtp();
        String statusStr = transactionRequestModel.getStatus();
        String remark = transactionRequestModel.getRemark();

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
                EssentialDetailsModel essentialDetailsModel = essentialDetailsRepository.findById(detailsId).get();
                long userId = essentialDetailsModel.getUserId();
                UserModel userModel = userRepository.findById(userId).get();
                long lastFourDigitsOfPan = userModel.getLastFourDigitsOfPan();

                if (receivedOtp == lastFourDigitsOfPan) {
                    // Initiate the txn
                    UUID trackId = essentialDetailsModel.getTrackId();
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
    public ResponseEntity<TransactionResponse> ConfirmTxn(TransactionRequestModel transactionRequestModel) {
        UUID detailsId = transactionRequestModel.getDetailsId();
        Long receivedOtp = transactionRequestModel.getOtp();
        String statusStr = transactionRequestModel.getStatus();
        String remark = transactionRequestModel.getRemark();
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
            EssentialDetailsModel essentialDetailsModel = essentialDetailsRepository.findById(detailsId).get();
            boolean status=true;
            String remark="";
            Integer cnt=0;
            if(essentialDetailsModel == null) {
                status = false;
                remark = "Invalid Transaction ID";
            } else {
                Integer txnCount = essentialDetailsModel.getTxnCount();
                Integer PanOTPCount = essentialDetailsModel.getPanOTPCount();
                Integer MobOTPCount = essentialDetailsModel.getMobOTPCount();

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
                        Date d1 = essentialDetailsModel.getCreatedAt();
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

            EssentialDetailsModel essentialDetailsModel = essentialDetailsRepository.findById(detailsId).get();
            String detailsStatus = essentialDetailsModel.getStatus();
            if(detailsStatus != null) {
                if (detailsStatus.equalsIgnoreCase("SUCCESS") || detailsStatus.equalsIgnoreCase("FAIL")) {
                    status = true;
                }
            }
            return status;
        }
}
