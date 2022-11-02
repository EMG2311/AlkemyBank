package com.alkemy.wallet.service;

import com.alkemy.wallet.model.Transaction;

public interface TransactionService {
    Transaction getTransactionDetailById (Integer Id) throws Exception;
}
