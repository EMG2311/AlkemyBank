package com.alkemy.wallet.service.implementation;

import com.alkemy.wallet.dto.*;
import com.alkemy.wallet.exception.ResourceNotFoundException;
import com.alkemy.wallet.exception.InvalidAmountException;
import com.alkemy.wallet.exception.TransactionLimitExceededException;
import com.alkemy.wallet.mapper.AccountMapper;
import com.alkemy.wallet.mapper.TransactionMapper;
import com.alkemy.wallet.model.Account;
import com.alkemy.wallet.model.Transaction;
import com.alkemy.wallet.model.User;
import com.alkemy.wallet.repository.TransactionRepository;
import com.alkemy.wallet.service.AccountService;
import com.alkemy.wallet.service.TransactionService;
import com.alkemy.wallet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private UserService userService;

    @Override
    public TransactionDetailDto getTransactionDetailById(Integer transactionId, String userToken ) throws ResourceNotFoundException {
        var transaction = transactionRepository.findById(transactionId);
        if(transaction.isPresent()){
            User user = getUserByTransactionId(transactionId);
            userService.matchUserToToken(user.getUserId(),userToken);
            return transactionMapper.convertToTransactionDetailDto(transaction.get());
        }else{
            throw new ResourceNotFoundException("Transaction does not exist");
        }
    }

    @Override
    public TransactionDepositDto createDeposit(TransactionDepositRequestDto transactionDepositRequestDto) {
        Double newTransactionAmount = transactionDepositRequestDto.getAmount();
        accountService.increaseBalance(transactionDepositRequestDto.getAccountId(), newTransactionAmount);


        AccountDto accountDto = accountService.getAccountById(transactionDepositRequestDto.getAccountId());
        TransactionDepositDto transactionDepositDto = new TransactionDepositDto(
                newTransactionAmount,
                transactionDepositRequestDto.getDescription());

        // It would be nice to have an exception handler. We should implement it in a separate branch
        if(newTransactionAmount <= 0) {
            throw new InvalidAmountException("The amount must be greater than 0");
        }

        if(newTransactionAmount > accountDto.transactionLimit()){
            throw new TransactionLimitExceededException("The transaction limit of " + accountDto.transactionLimit() + " was exceeded by a deposit of " + newTransactionAmount);
        }


        transactionDepositDto.setAccount(accountMapper.convertToEntity(accountDto));
        Transaction newTransaction = transactionRepository.save(transactionMapper.convertToEntity(transactionDepositDto));

        return transactionMapper.convertToTransactionDepositDto(newTransaction);
    }

    @Override
    public List<TransactionDetailDto> getTransactions(Integer userId) {
        List<Transaction> transactions = transactionRepository.findAll();
        List<Transaction> transactionsOfUser = transactions
                .stream()
                .filter(transaction ->
                        transaction.getAccount().getUser().getUserId().equals(userId))
                .toList();

        if(transactionsOfUser.isEmpty()){
            throw new ResourceNotFoundException("The user with id " +  userId +" has no transactions");
        }

        return convertTransactionListToDto(transactionsOfUser);

    }
    @Override
    public List<TransactionDetailDto> getTransactionsByAccount(Integer accountId) {
        List<Transaction> transactionsOfAccount = transactionRepository.findAllByAccountId(new Account(accountId));
        return convertTransactionListToDto(transactionsOfAccount);
    }


    @Override
    public TransactionPaymentDto createPayment(TransactionPaymentRequestDto transactionPaymentRequestDto) {
        Double newTransactionAmount = transactionPaymentRequestDto.getAmount();
        accountService.reduceBalance(transactionPaymentRequestDto.getAccountId(), newTransactionAmount);


        AccountDto accountDto = accountService.getAccountById(transactionPaymentRequestDto.getAccountId());
        TransactionPaymentDto transactionPaymentDto = new TransactionPaymentDto(
                newTransactionAmount,
                transactionPaymentRequestDto.getDescription());

        // It would be nice to have an exception handler. We should implement it in a separate branch
        if (newTransactionAmount <= 0) {
            throw new InvalidAmountException("The amount must be greater than 0");
        }

        if (newTransactionAmount > accountDto.transactionLimit()) {
            throw new TransactionLimitExceededException("The transaction limit of " + accountDto.transactionLimit() + " was exceeded by a payment of " + newTransactionAmount);
        }


        transactionPaymentDto.setAccount(accountMapper.convertToEntity(accountDto));
        Transaction newTransaction = transactionRepository.save(transactionMapper.convertToEntity(transactionPaymentDto));

        return transactionMapper.convertToTransactionPaymentDto(newTransaction);
    }

    public User getUserByTransactionId(Integer id) {
        var t = transactionRepository.findById(id);
        if(t.isPresent()){
            return t.get().getAccount().getUser();
        }else {
            throw new ResourceNotFoundException("Transaction does not exist");
        }
    }

    private List<TransactionDetailDto> convertTransactionListToDto(List<Transaction> transactions){
        return transactions
                .stream()
                .map(transaction -> transactionMapper.convertToTransactionDetailDto(transaction))
                .toList();

    }

    @Override
    public TransactionDetailDto updateTransaction(TransactionPatchDto transactionPatch, Integer Id) throws Exception {
        var transaction = transactionRepository.findById(Id).orElseThrow(Exception::new);
        transaction.setDescription(transactionPatch.description());

        return transactionMapper.convertToTransactionDetailDto(transactionRepository.save(transaction));
    }
}