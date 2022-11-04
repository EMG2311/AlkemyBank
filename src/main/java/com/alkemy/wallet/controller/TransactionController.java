package com.alkemy.wallet.controller;



import com.alkemy.wallet.dto.AccountDto;
import com.alkemy.wallet.dto.TransactionDepositDto;
import com.alkemy.wallet.dto.TransactionDepositRequestDto;
import com.alkemy.wallet.dto.TransactionDetailDto;
import com.alkemy.wallet.exception.InvalidAmountException;
import com.alkemy.wallet.mapper.AccountMapper;
import com.alkemy.wallet.mapper.TransactionMapper;
import com.alkemy.wallet.mapper.UserMapper;
import com.alkemy.wallet.model.Account;
import com.alkemy.wallet.model.Transaction;
import com.alkemy.wallet.service.AccountService;
import com.alkemy.wallet.service.TransactionService;
import com.alkemy.wallet.service.UserService;
import com.alkemy.wallet.service.implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private TransactionMapper transactionMapper;

    @GetMapping( value = "/{id}")
    @PreAuthorize("hasRole('USER_ROLE')")
    public ResponseEntity<TransactionDetailDto> getTransactionDetailById(@PathVariable("id") Integer id ) throws Exception {
        return ResponseEntity.ok(transactionService.getTransactionDetailById(id));
    }

    @PostMapping( value = "/deposit" )
    public ResponseEntity<TransactionDepositDto> createDeposit(@RequestBody TransactionDepositRequestDto transactionDepositRequestDto) {
        TransactionDepositDto transactionDepositDto = new TransactionDepositDto(transactionDepositRequestDto.getAmount(), transactionDepositRequestDto.getDescription());
        AccountDto accountDto = accountService.getAccountById(transactionDepositRequestDto.getAccountId());

        transactionDepositDto.setAccount(accountMapper.convertToEntity(accountDto));

        Transaction transactionCreated = transactionService.createDeposit(transactionMapper.convertToEntity(transactionDepositDto));

        return ResponseEntity.ok(transactionMapper.convertToTransactionDepositDto(transactionCreated));
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<Object> handleAmountException(Exception e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
