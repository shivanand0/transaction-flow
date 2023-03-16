package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.error.LenderException;
import com.flexmoney.transactionflow.model.*;
import com.flexmoney.transactionflow.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LenderService implements ILenderService {

    @Autowired
    private ILenderRepository lenderRepository;

    @Autowired
    private ILenderInfoRepository lenderInfoRepository;

    @Autowired
    private ILenderIdRepository lenderIdRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private TransactionFlowService transactionFlowService;

    @Autowired
    private IEssentialDetailsRepository essentialDetailsRepository;


    @Override
    public void addLender(LenderInfoRequestModel lenderInfoRequestModel) throws LenderException {
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
        LenderInfoModel savedLenderInfoModel = lenderInfoRepository.save(lenderInfo);
        if (savedLenderInfoModel == null) {
            log.error("Unable to add the lender: {}", lender.getLenderName());
            throw new LenderException("Unable to add the lender", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public DetailsModel getDetails(UUID detailsId) throws LenderException {
        EssentialDetailsModel essentialDetails = essentialDetailsRepository.findById(detailsId).get();
        if (essentialDetails == null) {
            log.error("Invalid transaction");
            throw new LenderException("Invalid Transaction", HttpStatus.BAD_REQUEST.value());
        }

        String userMobileNumber = essentialDetails.getMobileNumber();
        double userTxnAmount = essentialDetails.getAmount();

        List<LenderIdModel> userEligibleLendersList = lenderIdRepository.findAllByUserMobileNumber(userMobileNumber).stream().filter(e -> e.getCreditLimit() >= userTxnAmount).collect(Collectors.toList());
        if (userEligibleLendersList == null) {
            log.error("User does not have enough credit for this transaction with mobile number: {}", userMobileNumber);
            throw new LenderException("User does not have enough credit for this transaction", HttpStatus.BAD_REQUEST.value());
        }

        boolean txnCountCheck = transactionFlowService.checkTxnCountValues(detailsId, "txncount");
        if (txnCountCheck == false) {
            log.error("Transaction timeout occurred with txn id: {}", detailsId);
            throw new LenderException("Transaction Timeout", HttpStatus.BAD_REQUEST.value());
        }

        boolean isTxnCompletedCheck = transactionFlowService.checkIfTxnIsCompleted(detailsId);
        if (isTxnCompletedCheck == true) {
            log.error("This transaction is completed with txn id: {}", detailsId);
            throw new LenderException("This transaction is completed!", HttpStatus.BAD_REQUEST.value());
        }

        Long userId = essentialDetails.getUserId();
        List<Integer> userPreApprovedLenders = new ArrayList<>();
        userEligibleLendersList.forEach(lender -> userPreApprovedLenders.add(lender.getLenderId()));


        List<LenderDetailsModel> lenderDetailsModelList = new ArrayList<>();

        for (Integer lenderID : userPreApprovedLenders) {

            List<LenderInfoModel> lenderInfo = lenderInfoRepository.findAllByLenderId(lenderID);
            if (lenderInfo == null) {
                log.error("Unable to fetch lender info with lender id: {}", lenderID);
                throw new LenderException("System Error! We are experiencing technical difficulties & are working to fix them. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            List<EmiDetailsModel> emiDetailsModelList = new ArrayList<>();

            for (LenderInfoModel lenderI : lenderInfo) {
                double principal = userTxnAmount;
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

            LenderModel lenderModel = lenderRepository.findById(lenderID).get();
            if (lenderModel == null) {
                log.error("Unable to fetch lender with lender id: {}", lenderID);
                throw new LenderException("System Error! We are experiencing technical difficulties & are working to fix them. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

            LenderDetailsModel lenderDetailsModel = LenderDetailsModel.builder()
                    .lenderId(lenderID)
                    .lenderName(lenderModel.getLenderName())
                    .primaryLogoUrl(lenderModel.getPrimaryLogoUrl())
                    .secondaryLogoUrl(lenderModel.getSecondaryLogoUrl())
                    .lenderType("EMI")
                    .emiDetailsModelList(emiDetailsModelList)
                    .build();

            lenderDetailsModelList.add(lenderDetailsModel);
        }
        String userName = userRepository.findById(userId).get().getUserName();
        if (userName == null) {
            log.error("Unable to fetch user details with user id: {}", userId);
            throw new LenderException("System Error! We are experiencing technical difficulties & are working to fix them. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return DetailsModel.builder()
                .statusCode(HttpStatus.OK.value())
                .status(true)
                .userName(userName)
                .mobileNumber(userMobileNumber)
                .amount(userTxnAmount)
                .lenderDetailsModelList(lenderDetailsModelList)
                .build();

    }
}
