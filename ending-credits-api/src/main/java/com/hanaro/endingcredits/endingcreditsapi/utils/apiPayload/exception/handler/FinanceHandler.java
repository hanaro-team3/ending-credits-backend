package com.hanaro.endingcredits.endingcreditsapi.utils.apiPayload.exception.handler;

import com.hanaro.endingcredits.endingcreditsapi.utils.apiPayload.code.BaseErrorCode;
import com.hanaro.endingcredits.endingcreditsapi.utils.apiPayload.exception.GeneralException;

public class FinanceHandler extends GeneralException {
    public FinanceHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
