package com.geunskoo.hexagonalarchitecture.domain;

import java.util.Optional;
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

    public static Account withId(AccountId id, Money baselineBalance, ActivityWindow activityWindow) {
        return new Account(id, baselineBalance, activityWindow);
    }

    public static Account withoutId(Money baselineBalance, ActivityWindow activityWindow) {
        return new Account(null, baselineBalance, activityWindow);
    }

    public Optional<AccountId> getId() {
        return Optional.ofNullable(this.id);
    }

    @Value
    public class AccountId {

        private Long value;
    }
}
