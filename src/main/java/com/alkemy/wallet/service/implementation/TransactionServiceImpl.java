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
    public Transaction getTransactionDetailById(Integer Id) throws Exception {
        var transaction = Optional.of(repository.getById(Id));
        if(transaction.isPresent()){
            return transaction.get();
        }else{
            throw new Exception("This transaction does not exist");
        }
    }

}
