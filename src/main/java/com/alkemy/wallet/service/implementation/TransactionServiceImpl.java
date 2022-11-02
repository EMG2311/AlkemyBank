package com.alkemy.wallet.service.implementation;

import com.alkemy.wallet.model.Transaction;
import com.alkemy.wallet.repository.TransactionRepository;
import com.alkemy.wallet.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private TransactionRepository repository ;

    @Override
    public Optional<Transaction> getTransactionDetailById(Integer Id){
        var transaction = Optional.of(repository.getById(Id)).orElseThrow();
        return Optional.of(transaction);
    }

}
