package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.TransactionDepositDto;
import com.alkemy.wallet.dto.TransactionDepositRequestDto;
import com.alkemy.wallet.dto.TransactionDetailDto;
import java.util.List;

public interface TransactionService {
    TransactionDetailDto getTransactionDetailById (Integer Id) throws Exception;

    TransactionDepositDto createDeposit(TransactionDepositRequestDto transactionDepositRequestDto);

    List<TransactionDetailDto> getTransactions(Integer userId);
}
