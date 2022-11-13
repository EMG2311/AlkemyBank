package com.alkemy.wallet.controller.transaction;

import com.alkemy.wallet.dto.AccountBalanceDto;
import com.alkemy.wallet.dto.AccountDto;
import com.alkemy.wallet.dto.FixedTermDepositDto;
import com.alkemy.wallet.mapper.AccountMapper;
import com.alkemy.wallet.mapper.FixedTermDepositMapper;
import com.alkemy.wallet.model.*;
import com.alkemy.wallet.model.Currency;
import com.alkemy.wallet.repository.FixedTermDepositRepository;
import com.alkemy.wallet.repository.UserRepository;
import com.alkemy.wallet.security.JWTUtil;
import com.alkemy.wallet.service.AccountService;
import com.alkemy.wallet.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetBalanceEndpointTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private FixedTermDepositRepository fixedTermDepositRepository;

    @MockBean
    private JWTUtil jwtUtilMocked;

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
    
    private List<FixedTermDepositDto> usdFixedTermDepositsDto, arsFixedTermDepositsDto;

    private List<AccountDto> userAccounts;

    @BeforeEach
    void setUp() {
        Role userRole = new Role(RoleName.USER, "user", timestamp, timestamp);

        user1 = new User(1, "Pepe", "Lopez", "lopez@example.com", "123", userRole, timestamp, timestamp, false);

//        when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
//        when(userRepository.findById(user1.getUserId())).thenReturn(Optional.of(user1));
        //token1 = jwtUtil.generateToken(user1);

        when(jwtUtilMocked.extractClaimUsername(token1.substring(7))).thenReturn(user1.getEmail());
        when(userService.loadUserByUsername(user1.getEmail())).thenReturn(user1);


        arsAccount = new AccountDto(1,user1.getUserId(), 20000.0, Currency.ARS, 70000.0, timestamp, timestamp, false);
        usdAccount = new AccountDto(2, user1.getUserId(), 8000.0, Currency.USD, 10000.0, timestamp, timestamp, false);
        userAccounts = new ArrayList<>();
        userAccounts.add(arsAccount);
        userAccounts.add(usdAccount);
        
        when(accountService.getAccountsByUserId(user1.getUserId(), token1)).thenReturn(userAccounts);

        fixedTermDepositArs1 = new FixedTermDeposit(1, 1000.0, accountMapper.convertToEntity(arsAccount), 50.0, timestamp, timestamp);
        fixedTermDepositArs2 = new FixedTermDeposit(1, 2000.0, accountMapper.convertToEntity(arsAccount), 50.0, timestamp, timestamp);
        fixedTermDepositUsd = new FixedTermDeposit(1, 3000.0, accountMapper.convertToEntity(usdAccount), 5.0, timestamp, timestamp);


        arsFixedTermDepositsDto = Arrays.asList(fixedTermDepositMapper.convertToDto(fixedTermDepositArs1), fixedTermDepositMapper.convertToDto(fixedTermDepositArs2));
        usdFixedTermDepositsDto = List.of(fixedTermDepositMapper.convertToDto(fixedTermDepositUsd));



//        when(fixedTermDepositRepository.findByAccount_AccountId(arsAccount.id())).thenReturn(arsFixedTermDeposits);
//        when(fixedTermDepositRepository.findByAccount_AccountId(usdAccount.id())).thenReturn(usdFixedTermDeposits);


    }

    @Test
    void getUserBalance() throws  Exception{

        List<AccountBalanceDto> expectedListAccountBalanceDto = new LinkedList<>();
        AccountBalanceDto accountBalanceDtoArs = accountMapper.convertAccountDtoToAccountBalanceDto(arsAccount);
        AccountBalanceDto accountBalanceDtoUsd = accountMapper.convertAccountDtoToAccountBalanceDto(usdAccount);

        accountBalanceDtoArs.setFixedTermDeposits(arsFixedTermDepositsDto);
        accountBalanceDtoUsd.setFixedTermDeposits(usdFixedTermDepositsDto);

//        when(accountService.getAccountBalance(arsAccount)).thenReturn(accountBalanceDtoArs);
//        when(accountService.getAccountBalance(usdAccount)).thenReturn(accountBalanceDtoUsd);

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
                .andExpect(jsonPath("$[0].currency").value(accountBalanceDtoArs.getCurrency()))
                .andExpect(jsonPath("$[1].currency").value(accountBalanceDtoUsd.getCurrency()))
        ;
    }


}
