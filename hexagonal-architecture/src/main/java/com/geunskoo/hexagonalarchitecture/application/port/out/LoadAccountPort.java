package com.geunskoo.hexagonalarchitecture.application.port.out;

import com.geunskoo.hexagonalarchitecture.domain.Account;
import com.geunskoo.hexagonalarchitecture.domain.Account.AccountId;
import java.time.LocalDateTime;

public interface LoadAccountPort {

    Account loadAccount(AccountId accountId, LocalDateTime baselineDate);
}
