package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.error.LenderException;
import com.flexmoney.transactionflow.model.DetailsModel;
import com.flexmoney.transactionflow.model.LenderInfoRequestModel;

import java.util.UUID;

public interface ILenderService {
    void addLender(LenderInfoRequestModel lenderInfoRequestModel) throws LenderException;

    DetailsModel getDetails(UUID detailsId) throws LenderException;
}
