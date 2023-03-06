package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.DetailsModel;
import com.flexmoney.transactionflow.model.LenderInfoModel;
import com.flexmoney.transactionflow.model.LenderInfoRequestModel;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ILenderService {
    LenderInfoModel addLender(LenderInfoRequestModel lenderInfoRequestModel);
    ResponseEntity<DetailsModel> getDetails(UUID detailsId);
}
