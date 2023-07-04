package com.geunskoo.hexagonalarchitecture.domain;

import static com.geunskoo.common.AccountTestData.defaultAccount;
import static com.geunskoo.common.ActivityTestData.defaultActivity;
import static org.assertj.core.api.Assertions.*;

import com.geunskoo.hexagonalarchitecture.domain.Account.AccountId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccountTest {

    @Test
    @DisplayName("계좌 인출 성공 테스트.")
    void withdrawSucceeds() {
        //given
        AccountId accountId = new AccountId(3L);
        Account account = defaultAccount()
            .withAccountId(accountId)
            .withBaselineBalance(Money.of(500L))
            .withActivityWindow(new ActivityWindow(
                defaultActivity()
                    .withTargetAccount(accountId)
                    .withMoney(Money.of(999L)).build(),
                defaultActivity()
                    .withTargetAccount(accountId)
                    .withMoney(Money.of(1L)).build()
            )).build();

        //when
        boolean success = account.withdraw(Money.of(500), new AccountId(4L));

        //then
        assertThat(success).isTrue();
        assertThat(account.getActivityWindow().getActivityList()).hasSize(3);
        assertThat(account.calculateBalance()).isEqualTo(Money.of(1000L));
    }

    @Test
    @DisplayName("계좌 인출 실패 테스트.")
    void withdrawFailure() {
        //given
        AccountId accountId = new AccountId(3L);
        Account account = defaultAccount()
            .withAccountId(accountId)
            .withBaselineBalance(Money.of(500L))
            .withActivityWindow(new ActivityWindow(
                defaultActivity()
                    .withTargetAccount(accountId)
                    .withMoney(Money.of(999L)).build(),
                defaultActivity()
                    .withTargetAccount(accountId)
                    .withMoney(Money.of(1L)).build()
            )).build();

        //when
        boolean failure = account.withdraw(Money.of(1501L), new AccountId(4L));

        //then
        assertThat(failure).isFalse();
        assertThat(account.getActivityWindow().getActivityList()).hasSize(2);
        assertThat(account.calculateBalance()).isEqualTo(Money.of(1500L));
    }

    @Test
    @DisplayName("계좌 예금 성공 테스트.")
    void depositSuccess() {
        //given
        AccountId accountId = new AccountId(3L);
        Account account = defaultAccount()
            .withAccountId(accountId)
            .withBaselineBalance(Money.of(500L))
            .withActivityWindow(new ActivityWindow(
                defaultActivity()
                    .withTargetAccount(accountId)
                    .withMoney(Money.of(999L)).build(),
                defaultActivity()
                    .withTargetAccount(accountId)
                    .withMoney(Money.of(1L)).build()
            )).build();
        //when
        boolean success = account.deposit(Money.of(500L), new AccountId(4L));

        //then
        assertThat(success).isTrue();
        assertThat(account.getActivityWindow().getActivityList()).hasSize(3);
        assertThat(account.calculateBalance()).isEqualTo(Money.of(2000L));
    }

    @Test
    @DisplayName("계좌 현재 잔액 불러오기 테스트.")
    void calculateBalance() {
        //given
        AccountId accountId = new AccountId(3L);
        Account account = defaultAccount()
            .withAccountId(accountId)
            .withBaselineBalance(Money.of(500L))
            .withActivityWindow(new ActivityWindow(
                defaultActivity()
                    .withTargetAccount(accountId)
                    .withMoney(Money.of(999L)).build(),
                defaultActivity()
                    .withTargetAccount(accountId)
                    .withMoney(Money.of(1L)).build()
            )).build();
        //when
        Money balance = account.calculateBalance();
        //then
        assertThat(balance).isEqualTo(Money.of(1500L));
    }
}