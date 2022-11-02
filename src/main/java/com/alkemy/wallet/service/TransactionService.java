package com.alkemy.wallet.service;

import com.alkemy.wallet.model.Transaction;

import java.util.Optional;

public interface TransactionService {
    Optional<Transaction> getTransactionDetailById (Integer Id);
}
