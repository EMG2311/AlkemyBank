package com.alkemy.wallet.service;

import com.alkemy.wallet.dto.FixedTermDepositDto;
import com.alkemy.wallet.exception.FixedTermDepositException;
import com.alkemy.wallet.model.FixedTermDeposit;

public interface FixedTermDepositService {
    FixedTermDeposit createFixedTermDeposit(FixedTermDepositDto fixedTermDepositDto) throws FixedTermDepositException;

}
