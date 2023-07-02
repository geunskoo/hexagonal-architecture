package com.geunskoo.hexagonalarchitecture.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {

    private AccountId id;
    private Money baselineBalance;
    private ActivityWindow activityWindow;

    public Account withId(AccountId id, Money baselineBalance, ActivityWindow activityWindow) {
        return new Account(id, baselineBalance, activityWindow);
    }

    public Account withoutId(Money baselineBalance, ActivityWindow activityWindow) {
        return new Account(null, baselineBalance, activityWindow);
    }


    @Value
    public class AccountId {

        private Long value;
    }
}
