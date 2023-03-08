package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.error.LenderException;
import com.flexmoney.transactionflow.model.*;
import com.flexmoney.transactionflow.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
            throw new LenderException("Unable to add the lender", 500);
        }
    }

    @Override
    public ResponseEntity<DetailsModel> getDetails(UUID detailsId) {

        boolean check = transactionFlowService.checkTxnCountValues(detailsId, "txncount");
        if (check == false) {
            // throw error "You're not approved by our lenders for transaction";
            return new ResponseEntity<>(DetailsModel
                    .builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(false)
                    .message("Transaction Timeout")
                    .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        boolean check2 = transactionFlowService.checkIfTxnIsCompleted(detailsId);
        if (check2 == true) {
            return new ResponseEntity<>(DetailsModel
                    .builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(false)
                    .message("This transaction is completed!")
                    .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
        ;

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
}
