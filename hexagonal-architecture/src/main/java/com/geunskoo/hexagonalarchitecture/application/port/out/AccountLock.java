package com.geunskoo.hexagonalarchitecture.application.port.out;

import com.geunskoo.hexagonalarchitecture.domain.Account.AccountId;

public interface AccountLock {

    void lockAccount(AccountId accountId);

    void releaseAccount(AccountId accountId);
}
