package com.geunskoo.hexagonalarchitecture.application.port.out;

import com.geunskoo.hexagonalarchitecture.domain.Account;

public interface UpdateAccountStatePort {

    void updateActivities(Account account);
}
