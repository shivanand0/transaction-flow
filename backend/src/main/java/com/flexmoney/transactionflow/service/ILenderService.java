package com.flexmoney.transactionflow.service;

import com.flexmoney.transactionflow.model.LenderInfoDTO;
import com.flexmoney.transactionflow.model.ELenderInfo;

public interface ILenderService {
    ELenderInfo addLender(LenderInfoDTO lenderInfoDTO);
}
