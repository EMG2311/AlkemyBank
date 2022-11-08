package com.alkemy.wallet.controller;



import com.alkemy.wallet.dto.*;
import com.alkemy.wallet.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping(value = "/detail/{id}")
    @PreAuthorize("hasRole('USER_ROLE')")
    public ResponseEntity<TransactionDetailDto> getTransactionDetailById(@PathVariable("id") Integer transactionId, @RequestHeader("Authorization") String userToken ) throws Exception {
        return ResponseEntity.ok(transactionService.getTransactionDetailById(transactionId, userToken));
    }

    @PostMapping( value = "/deposit" )
    public ResponseEntity<TransactionDepositDto> createDeposit(@RequestBody TransactionDepositRequestDto transactionDepositRequestDto) {
        return ResponseEntity.ok(transactionService.createDeposit(transactionDepositRequestDto));
    }

    @PostMapping( value = "/payment" )
    public ResponseEntity<TransactionPaymentDto> createPayment(@RequestBody TransactionPaymentRequestDto transactionPaymentRequestDto) {
        return ResponseEntity.ok(transactionService.createPayment(transactionPaymentRequestDto));
    }

    @GetMapping(value = "/all/{userId}")
//    @PreAuthorize("hasRole('USER_ROLE')")
    public ResponseEntity<List<TransactionDetailDto>> listTransactions(@PathVariable Integer userId) {
        return ResponseEntity.ok(transactionService.getTransactions(userId));
    }

    @PatchMapping(value="/{id}")
    ResponseEntity<TransactionDetailDto> updateTransaction(@RequestBody TransactionPatchDto transactionPatchDto, @PathVariable Integer id, @RequestHeader("Authorization") String userToken) throws Exception{
        return ResponseEntity.ok(transactionService.updateTransaction(transactionPatchDto,id,userToken));
    }

    @PostMapping( value = "/sendArs" )
    public ResponseEntity<TransactionDetailDto> sendArs(@RequestBody TransactionTransferRequestDto transactionTransferRequestDto,
                                                         @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(transactionService.sendArs(token, transactionTransferRequestDto));
    }

    @PostMapping( value = "/sendUsd" )
    public ResponseEntity<TransactionDetailDto> sendUsd(@RequestBody TransactionTransferRequestDto transactionTransferRequestDto,
                                                         @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(transactionService.sendUsd(token, transactionTransferRequestDto));
    }
}
