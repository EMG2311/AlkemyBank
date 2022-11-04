package com.alkemy.wallet.service.implementation;

import com.alkemy.wallet.dto.FixedTermDepositDto;
import com.alkemy.wallet.exception.FixedTermDepositException;
import com.alkemy.wallet.mapper.AccountMapper;
import com.alkemy.wallet.mapper.FixedTermDepositMapper;
import com.alkemy.wallet.model.FixedTermDeposit;
import com.alkemy.wallet.repository.FixedTermDepositRepository;
import com.alkemy.wallet.security.JWTUtil;
import com.alkemy.wallet.service.FixedTermDepositService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;

@Service
@Transactional()
@RequiredArgsConstructor
public class FixedTermDepositServiceImpl implements FixedTermDepositService {
    @Autowired
    private FixedTermDepositRepository fixedTermDepositRepository;
    private final FixedTermDepositMapper mapper;
    private AccountMapper accountMapper;
    private JWTUtil jwtUtil;
    private UserServiceImpl userService;
    private AccountServiceImpl accountService;

    @Override
    public FixedTermDepositDto createFixedTermDeposit(FixedTermDepositDto fixedTermDepositDto, String token) throws FixedTermDepositException {
        FixedTermDeposit fixedTermDeposit=new FixedTermDeposit(mapper.convertToEntity(fixedTermDepositDto));
        Timestamp timestamp=new Timestamp(new Date().getTime());
        fixedTermDeposit.setCreationDate(timestamp);

        Long days= ((fixedTermDeposit.getClosingDate().getTime())-fixedTermDeposit.getCreationDate().getTime())/86400000 ;
           if(days<30){
               throw new FixedTermDepositException();
           }

        fixedTermDeposit.setAccount(accountService.findAccountByUserIdAndCurrency(userService.loadUserByUsername(jwtUtil.extractClaimUsername(token)),fixedTermDepositDto.getCurrency()));
        fixedTermDeposit.setInterest(0.5*days);
        fixedTermDepositRepository.save(fixedTermDeposit);
        return fixedTermDepositDto;

    }
}