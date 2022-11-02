package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.TransactionDetailDto;

public interface TransactionService {
    TransactionDetailDto getTransactionDetailById (Integer Id) throws Exception;
}
