package com.alkemy.wallet.controller.transaction;

import com.alkemy.wallet.dto.AccountBalanceDto;
import com.alkemy.wallet.dto.AccountDto;
import com.alkemy.wallet.dto.FixedTermDepositDto;
import com.alkemy.wallet.mapper.AccountMapper;
import com.alkemy.wallet.mapper.FixedTermDepositMapper;
import com.alkemy.wallet.model.*;
import com.alkemy.wallet.model.Currency;
import com.alkemy.wallet.repository.AccountRepository;
import com.alkemy.wallet.repository.FixedTermDepositRepository;
import com.alkemy.wallet.repository.UserRepository;
import com.alkemy.wallet.security.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetBalanceEndpointTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private FixedTermDepositRepository fixedTermDepositRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    FixedTermDepositMapper fixedTermDepositMapper;

    Timestamp timestamp = new Timestamp(new Date().getTime());

    User user1;

    String token1 = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsb3BlekBleGFtcGxlLmNvbSIsImV4cCI6MTY2ODM4MzM1MywiaWF0IjoxNjY4Mzc5NzUzfQ.duw1ox0xzEzsUwecUkoC0DPRTGIw5oaWZCIdu_lUztM";

    FixedTermDeposit fixedTermDepositArs1, fixedTermDepositArs2, fixedTermDepositUsd;

    AccountDto arsAccount, usdAccount;
    Account acc1, acc2;

    private List<FixedTermDepositDto> usdFixedTermDepositsDto, arsFixedTermDepositsDto;

    private List<AccountDto> userAccounts;

    @BeforeEach
    void setUp() {
        Role userRole = new Role(RoleName.USER, "user", timestamp, timestamp);

        user1 = new User(1, "Marcos", "Fernandez", "email@email", "kjkjkjw", userRole, timestamp, timestamp, false);

        token1 = jwtUtil.generateToken(user1);
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
        when(userRepository.findById(user1.getUserId())).thenReturn(Optional.of(user1));


        acc1 = new Account(1, Currency.ARS, 10000d, 75000d, user1, timestamp, timestamp, false);
        acc2 = new Account(2, Currency.USD, 2000d, 35000d, user1, timestamp, timestamp, false);
        when(accountRepository.findAccountsByUserId(user1)).thenReturn(List.of(acc1,acc2));

        arsAccount = new AccountDto(1, user1.getUserId(), 75000d, Currency.ARS, 10000d, timestamp, timestamp, false);
        usdAccount = new AccountDto(2, user1.getUserId(), 35000d, Currency.USD, 2000d, timestamp, timestamp, false);

        fixedTermDepositArs1 = new FixedTermDeposit(1, 1000.0, acc1, 50.0, timestamp, timestamp);
        fixedTermDepositArs2 = new FixedTermDeposit(2, 2000.0, acc1, 50.0, timestamp, timestamp);
        fixedTermDepositUsd = new FixedTermDeposit(3, 3000.0, acc2, 5.0, timestamp, timestamp);

        when(fixedTermDepositRepository.findByAccount_AccountId(acc1.getAccountId())).thenReturn(List.of(fixedTermDepositArs1, fixedTermDepositArs2));
        when(fixedTermDepositRepository.findByAccount_AccountId(acc2.getAccountId())).thenReturn(List.of(fixedTermDepositUsd));

        arsFixedTermDepositsDto = List.of(fixedTermDepositMapper.convertToDto(fixedTermDepositArs1), fixedTermDepositMapper.convertToDto(fixedTermDepositArs2));
        usdFixedTermDepositsDto = List.of(fixedTermDepositMapper.convertToDto(fixedTermDepositUsd));

    }

    @Test
    void getUserBalance() throws Exception {

        List<AccountBalanceDto> expectedListAccountBalanceDto = new LinkedList<>();
        AccountBalanceDto accountBalanceDtoArs = accountMapper.convertAccountDtoToAccountBalanceDto(arsAccount);
        AccountBalanceDto accountBalanceDtoUsd = accountMapper.convertAccountDtoToAccountBalanceDto(usdAccount);

        accountBalanceDtoArs.setFixedTermDeposits(arsFixedTermDepositsDto);
        accountBalanceDtoUsd.setFixedTermDeposits(usdFixedTermDepositsDto);

        expectedListAccountBalanceDto.add(accountBalanceDtoArs);
        expectedListAccountBalanceDto.add(accountBalanceDtoUsd);


        mockMvc.perform(get("/accounts/balance")
                        .header("Authorization", "Bearer " + token1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(accountBalanceDtoArs.getId()))
                .andExpect(jsonPath("$[1].id").value(accountBalanceDtoUsd.getId()))
                .andExpect(jsonPath("$[0].balance").value(accountBalanceDtoArs.getBalance()))
                .andExpect(jsonPath("$[1].balance").value(accountBalanceDtoUsd.getBalance()))
                .andExpect(jsonPath("$[0].currency").value(accountBalanceDtoArs.getCurrency().toString()))
                .andExpect(jsonPath("$[1].currency").value(accountBalanceDtoUsd.getCurrency().toString()))
                .andExpect(jsonPath("$[0].fixedTermDeposits[0].amount").value(accountBalanceDtoArs.getFixedTermDeposits().get(0).getAmount()))
                .andExpect(jsonPath("$[0].fixedTermDeposits[1].amount").value(accountBalanceDtoArs.getFixedTermDeposits().get(1).getAmount()))
                .andExpect(jsonPath("$[1].fixedTermDeposits[0].amount").value(accountBalanceDtoUsd.getFixedTermDeposits().get(0).getAmount()))
        ;
    }


}
