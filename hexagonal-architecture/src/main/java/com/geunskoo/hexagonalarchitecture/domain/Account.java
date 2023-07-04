package com.geunskoo.hexagonalarchitecture.domain;

import java.time.LocalDateTime;
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

    public boolean withdraw(Money money, AccountId targetAccountId) {
        if (!mayWithdraw(money)) {
            return false;
        }
        Activity withdrawal = new Activity(this.id, this.id, targetAccountId, LocalDateTime.now(), money);
        this.activityWindow.addActivity(withdrawal);
        return true;
    }

    public boolean deposit(Money money, AccountId sourceAccountId) {
        Activity deposit = new Activity(this.id, sourceAccountId, this.id, LocalDateTime.now(), money);
        this.activityWindow.addActivity(deposit);
        return true;
    }

    public Money calculateBalance() {
        return Money.add(this.baselineBalance, this.activityWindow.calculateBalance(this.id));
    }

    private boolean mayWithdraw(Money money) {
        return Money.add(this.calculateBalance(), money.negate())
            .isPositiveOrZero();
    }

    @Value
    public static class AccountId {

        private Long value;
    }
}
