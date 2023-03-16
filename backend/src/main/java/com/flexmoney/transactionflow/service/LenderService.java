package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.core.error.LenderException;
import com.flexmoney.transactionflow.error.ApiResponseCodeEnum;
import com.flexmoney.transactionflow.error.ErrorDetails;
import com.flexmoney.transactionflow.model.ELender;
import com.flexmoney.transactionflow.model.ELenderInfo;
import com.flexmoney.transactionflow.model.LenderInfoDTO;
import com.flexmoney.transactionflow.repository.ILenderInfoRepository;
import com.flexmoney.transactionflow.repository.ILenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Service
public class LenderService implements ILenderService{
    @Autowired
    private ILenderInfoRepository lenderInfoRepository;

    @Autowired
    private ILenderRepository lenderRepository;
    @Override
    public ELenderInfo addLender(LenderInfoDTO lenderInfoDTO) {

        ELender lender = lenderRepository.findByLenderName(lenderInfoDTO.getLender().getLenderName());
        ELenderInfo lenderInfo;
        ELender ELender;

        if (lender != null) {
            throw new LenderException(ApiResponseCodeEnum.LENDER_NOT_FOUND.getCode(), "Lender not found", "Lender not found", HttpStatus.NOT_FOUND);
        } else {
            ELender = lenderInfoDTO.getLender();
        }

        lenderInfo = ELenderInfo.builder()
                .lender(ELender)
                .tenure(lenderInfoDTO.getTenure())
                .tenureType(lenderInfoDTO.getTenureType())
                .rateOfInterest(lenderInfoDTO.getRateOfInterest())
                .build();
        return lenderInfoRepository.save(lenderInfo);

    }
    @ExceptionHandler(LenderException.class)
    public ResponseEntity<ErrorDetails> handleException(LenderException e) {
        ErrorDetails errorDetailsResponse = new ErrorDetails(e.getCode(),e.getTitle(),e.getMessage(),e.getStatus().value());
        return new ResponseEntity<>(errorDetailsResponse, e.getStatus());
    }
}
