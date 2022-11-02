package com.alkemy.wallet.service.implementation;


import com.alkemy.wallet.dto.TransactionDetailDto;
import com.alkemy.wallet.dto.TransactionDepositDto;
import com.alkemy.wallet.exception.ResourceNotFoundException;
import com.alkemy.wallet.exception.InvalidAmountException;
import com.alkemy.wallet.model.Transaction;
import com.alkemy.wallet.repository.TransactionRepository;
import com.alkemy.wallet.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private TransactionRepository transactionRepository;

    @Override
    public TransactionDetailDto getTransactionDetailById(Integer Id) throws ResourceNotFoundException {
        var transaction = Optional.of(repository.getById(Id));
        if(transaction.isPresent()){
            return null; //transaction.stream()
                    //.map(mapper::convertToDto);
        }else{
            throw new ResourceNotFoundException("Transaction is empty");
        }
    }

    @Override
    public Transaction createDeposit(Transaction transaction) {
        Double amount = transaction.getAmount();

        // It would be nice to have an exception handler. We should implement it in a separate branch
        if(amount <= 0) {
            throw new InvalidAmountException();
        }

        return transactionRepository.save(transaction);
    }
}