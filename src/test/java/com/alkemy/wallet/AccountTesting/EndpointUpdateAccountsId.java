package com.alkemy.wallet.AccountTesting;

import com.alkemy.wallet.controller.AccountController;
import com.alkemy.wallet.dto.AccountDetailDto;
import com.alkemy.wallet.dto.AccountPatchDto;
import com.alkemy.wallet.dto.UserDto;
import com.alkemy.wallet.dto.UserRequestDto;
import com.alkemy.wallet.model.Currency;
import com.alkemy.wallet.service.AccountService;
import com.alkemy.wallet.service.implementation.AccountServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;

import static org.mockito.Mockito.when;
@Import(AccountController.class)
public class EndpointUpdateAccountsId {

    @MockBean
    private AccountServiceImpl accountServiceImpl;
    @InjectMocks
    private AccountController accountController;

    private AccountDetailDto accountDetailDto;
    private AccountPatchDto accountPatchDto;

    @BeforeEach
    void setUp(){
        accountServiceImpl = Mockito.mock(AccountServiceImpl.class);
        accountController = new AccountController(accountServiceImpl);

        accountDetailDto = new AccountDetailDto(1,3,200.0,Currency.ARS,1000.0,Timestamp.valueOf("2022-11-07 14:46:46.940000"),Timestamp.valueOf("2022-11-07 14:46:46.940000"));
        accountPatchDto = new AccountPatchDto(1010.00);

    }

    @Test
    void updateTransactionLimitFromAccount() throws Exception {
        Mockito.when(accountServiceImpl.updateAccount(accountPatchDto, accountDetailDto.getId(),"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYXBhQHBhcGEiLCJleHAiOjE2NjgwMjEwMzEsImlhdCI6MTY2ODAxNzQzMX0.4bYz_Zj36WKBl-MSDwuL95yqrir15KA1TJIbrHucExg")).thenReturn(accountDetailDto);
        ResponseEntity<AccountDetailDto> httpResponse = accountController.updateAccount(accountPatchDto, accountDetailDto.getId(),"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYXBhQHBhcGEiLCJleHAiOjE2NjgwMjEwMzEsImlhdCI6MTY2ODAxNzQzMX0.4bYz_Zj36WKBl-MSDwuL95yqrir15KA1TJIbrHucExg");
        Assertions.assertEquals(httpResponse.getStatusCode(), HttpStatus.OK);
        //accountDetailDto.setTransactionLimit(1010.00);
        System.out.println(accountDetailDto);
        System.out.println(httpResponse.getBody());
        if(accountDetailDto.equals(httpResponse.getBody())){
            System.out.println("igual");
        }
        Assertions.assertSame(accountDetailDto, httpResponse.getBody());
    }

    /*@PatchMapping(value="/{id}")
    ResponseEntity<AccountDetailDto> updateAccount(@RequestBody AccountPatchDto accountPatchDto, @PathVariable Integer id, @RequestHeader("Authorization") String userToken) throws Exception {
        return ResponseEntity.ok(accountService.updateAccount(accountPatchDto, id, userToken));
    }*/
}
