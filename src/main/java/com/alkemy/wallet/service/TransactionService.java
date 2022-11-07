package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.*;

import com.alkemy.wallet.model.Transaction;

import java.util.List;

public interface TransactionService {
    TransactionDetailDto getTransactionDetailById (Integer Id) throws Exception;

    TransactionDepositDto createDeposit(TransactionDepositRequestDto transactionDepositRequestDto);

    TransactionDetailDto updateTransaction(TransactionPatchDto transaction, Integer Id) throws Exception;

    List<TransactionDetailDto> getTransactions(Integer userId);

    TransactionPaymentDto createPayment(TransactionPaymentRequestDto transactionPaymentRequestDto);
}
