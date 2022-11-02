package com.alkemy.wallet.service.implementation;

import com.alkemy.wallet.model.Account;
import com.alkemy.wallet.model.Currency;
import com.alkemy.wallet.model.User;
import com.alkemy.wallet.repository.IAccountRepository;
import com.alkemy.wallet.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final IAccountRepository accountRepository;

    @Override
    public void createAccount(int userId, Currency currency) {
        User user = new User(userId);
        if(accountRepository.findAccountByUserIdAndCurrency(user, currency).isPresent()) {
            throw new RuntimeException("User already has an account for that currency.");
        }
        double balance = 0;
        double transactionLimit = getTransactionLimitForCurrency(currency);
        Date date = new Date();
        Timestamp creationDate = new Timestamp(date.getTime());
        Account account = new Account(user, currency, transactionLimit, balance, creationDate);
        accountRepository.save(account);
    }

    @Override
    public void reduceBalance(int accountId, double amount) {
        Optional<Account> account = accountRepository.findById(accountId);
        if(account.isEmpty()){
            throw new InvalidParameterException("Not found account.");
        }
        double oldBalance = account.get().getBalance();
        if(oldBalance < amount){
            throw new InvalidParameterException("The amount to reduce is bigger than the current balance.");
        }
        Account updatedAccount = account.get();
        updatedAccount.setBalance(oldBalance - amount);
        accountRepository.save(updatedAccount);
    }

    private double getTransactionLimitForCurrency(Currency currency){
        return switch (currency) {
            case ARS -> 300000;
            case USD -> 1000;
        };
    }
}
