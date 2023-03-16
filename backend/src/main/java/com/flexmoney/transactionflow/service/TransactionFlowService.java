package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.error.TransactionException;
import com.flexmoney.transactionflow.model.*;
import com.flexmoney.transactionflow.repository.IEssentialDetailsRepository;
import com.flexmoney.transactionflow.repository.ITrackStageRepository;
import com.flexmoney.transactionflow.repository.ITransactionRepository;
import com.flexmoney.transactionflow.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;


@Service
@Slf4j
public class TransactionFlowService implements ITransactionFlowService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private ITrackStageRepository trackStageRepository;

    @Autowired
    private ITransactionRepository transactionRepository;

    @Autowired
    private IEssentialDetailsRepository essentialDetailsRepository;


    @Override
    public TransactionResponse initiateTxn(TransactionRequestModel transactionRequestModel) throws TransactionException {
        UUID detailsId = transactionRequestModel.getDetailsId();
        Long receivedOtp = transactionRequestModel.getOtp();
        String statusStr = transactionRequestModel.getStatus();
        String remark = transactionRequestModel.getRemark();

        String msg;
        HttpStatus statusCode;

        boolean check2 = checkIfTxnIsCompleted(detailsId);
        if (check2 == true) {
            log.error("Transaction Completed Already! with detailsId: {}", detailsId);
            throw new TransactionException("Transaction Completed Already!", HttpStatus.BAD_REQUEST.value());
        } else {
            boolean check = checkTxnCountValues(detailsId, "panotp");
            if (!check) {
                // either block user for 24 hours
                // or mark txn as fail
                essentialDetailsRepository.updateStatusRemarkById(detailsId, "FAIL", "PAN-OTP-EXCEED");
                log.error("PAN-OTP-EXCEED for txn with detailsId: {}", detailsId);
                throw new TransactionException("PAN-OTP-EXCEED", HttpStatus.BAD_REQUEST.value());
            } else {
                EssentialDetailsModel essentialDetailsModel = essentialDetailsRepository.findById(detailsId).get();
                long userId = essentialDetailsModel.getUserId();
                UserModel userModel = userRepository.findById(userId).get();
                long lastFourDigitsOfPan = userModel.getLastFourDigitsOfPan();

                if (receivedOtp == lastFourDigitsOfPan) {
                    // Initiate the txn
                    UUID trackId = essentialDetailsModel.getDetailsId();
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
                } else {
                    log.error("Invalid PAN Details for txn with detailsId: {}", detailsId);
                    throw new TransactionException("Invalid PAN Details", HttpStatus.BAD_REQUEST.value());
                }
            }
        }


        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setStatus(true);
        transactionResponse.setStatusCode(statusCode.value());
        transactionResponse.setMessage(msg);

        return transactionResponse;
    }

    @Override
    public TransactionResponse confirmTxn(TransactionRequestModel transactionRequestModel) throws TransactionException {
        UUID detailsId = transactionRequestModel.getDetailsId();
        Long receivedOtp = transactionRequestModel.getOtp();
        String statusStr = transactionRequestModel.getStatus();
        String remark = transactionRequestModel.getRemark();
        long expectedMobileOtp = 1234;

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
            log.error("MOB-OTP-EXCEED for txn with detailsId: {}", detailsId);
            throw new TransactionException("MOB-OTP-EXCEED", HttpStatus.BAD_REQUEST.value());
        } else {
            if (receivedOtp == expectedMobileOtp) {
                transactionRepository.updateFieldsById(txnId, "SUCCESS");
                msg = "Success";
                statusCode = HttpStatus.ACCEPTED;
                essentialDetailsRepository.updateStatusRemarkById(detailsId, "SUCCESS", true + " " + remark);
            } else {
                log.error("Invalid OTP for txn with detailsId: {}", detailsId);
                throw new TransactionException("Invalid OTP", HttpStatus.BAD_REQUEST.value());
            }
        }

        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setStatus(true);
        transactionResponse.setStatusCode(statusCode.value());
        transactionResponse.setMessage(msg);

        return transactionResponse;
    }

    public boolean checkTxnCountValues(UUID detailsId, String checkFor) {
        // checkFor : txncount, panotp, mobileotp
        EssentialDetailsModel essentialDetailsModel = essentialDetailsRepository.findById(detailsId).get();
        boolean status = true;
        String remark = "";
        Integer cnt = 0;
        if (essentialDetailsModel == null) {
            status = false;
            remark = "Invalid Transaction ID";
        } else {
            Integer txnCount = essentialDetailsModel.getTxnCount();
            Integer PanOTPCount = essentialDetailsModel.getPanOTPCount();
            Integer MobOTPCount = essentialDetailsModel.getMobOTPCount();

            if (checkFor == "panotp") cnt = PanOTPCount;
            else if (checkFor == "mobileotp") cnt = MobOTPCount;

            if (cnt >= 3) {
                status = false;
                remark = "Rate limiter hit";
            } else {
                // Update count++
                if (checkFor == "txncount") {
                    ++txnCount;

                    // check the created timestamp with current timestamp
                    Date d1 = essentialDetailsModel.getCreatedAt();
                    Date d2 = new Date();

                    long difference_In_Time = d2.getTime() - d1.getTime();
                    long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;

                    if (difference_In_Minutes > 10) {
                        status = false;
                        remark = "transaction timeout";
                    } else {
                        status = true;
                        remark = "success";
                    }
                } else if (checkFor == "panotp") {
                    ++PanOTPCount;
                    status = true;
                    remark = "success";
                } else if (checkFor == "mobileotp") {
                    ++MobOTPCount;
                    status = true;
                    remark = "success";
                }

                essentialDetailsRepository.updateFieldsById(txnCount, PanOTPCount, MobOTPCount, detailsId);
            }
        }

        return status;
    }

    public boolean checkIfTxnExists(UUID detailsId) {
        boolean status = false;
        TransactionModel transactionModel = transactionRepository.findByDetailsId(detailsId);
        if (transactionModel != null) {
            // if txn entry with detailsID exists then return true
            status = true;
        }

        return status;
    }

    public boolean checkIfTxnIsCompleted(UUID detailsId) {
        boolean status = false;
        TransactionModel transactionModel = transactionRepository.findByDetailsId(detailsId);
        if (transactionModel != null) {
            String txnStatus = transactionModel.getStatus();
            if (txnStatus.equalsIgnoreCase("SUCCESS") || txnStatus.equalsIgnoreCase("FAIL")) {
                status = true;
            }
        }

        EssentialDetailsModel essentialDetailsModel = essentialDetailsRepository.findById(detailsId).get();
        String detailsStatus = essentialDetailsModel.getStatus();
        if (detailsStatus != null) {
            if (detailsStatus.equalsIgnoreCase("SUCCESS") || detailsStatus.equalsIgnoreCase("FAIL")) {
                status = true;
            }
        }
        return status;
    }
}
