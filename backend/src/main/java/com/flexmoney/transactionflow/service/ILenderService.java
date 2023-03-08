package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.error.LenderException;
import com.flexmoney.transactionflow.model.DetailsModel;
import com.flexmoney.transactionflow.model.LenderInfoRequestModel;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ILenderService {
    void addLender(LenderInfoRequestModel lenderInfoRequestModel) throws LenderException;

    ResponseEntity<DetailsModel> getDetails(UUID detailsId);
}
