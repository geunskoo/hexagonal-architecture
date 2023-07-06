package com.geunskoo.hexagonalarchitecture.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.geunskoo.hexagonalarchitecture.application.port.in.SendMoneyCommand;
import com.geunskoo.hexagonalarchitecture.application.port.out.AccountLock;
import com.geunskoo.hexagonalarchitecture.application.port.out.LoadAccountPort;
import com.geunskoo.hexagonalarchitecture.application.port.out.UpdateAccountStatePort;
import com.geunskoo.hexagonalarchitecture.domain.Account;
import com.geunskoo.hexagonalarchitecture.domain.Account.AccountId;
import com.geunskoo.hexagonalarchitecture.domain.Money;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

@DisplayName("SendMoney UseCase TEST")
class SendMoneyServiceTest {

    @Nested
    @DisplayName("송금하기 유스케이스 테스트")
    class SendMoney {

        private final LoadAccountPort loadAccountPort = Mockito.mock(LoadAccountPort.class);
        private final AccountLock accountLock = Mockito.mock(AccountLock.class);
        private final UpdateAccountStatePort updateAccountStatePort = Mockito.mock(UpdateAccountStatePort.class);
        private final SendMoneyService sendMoneyService = new SendMoneyService(
            loadAccountPort,
            accountLock,
            updateAccountStatePort
            , moneyTransferProperties()
        );

        @Test
        @DisplayName("내 계좌에서 출금하는 것이 실패할 때, 내계좌만 풀렸다 잠겨야한다.")
        void givenWithdrawalFails_thenOnlySourceAccountIsLockedAndReleased() {

            //given
            AccountId sourceAccountId = new AccountId(1L);
            AccountId targetAccountId = new AccountId(2L);
            Account sourceAccount = givenAnAccountWithId(sourceAccountId);
            Account targetAccount = givenAnAccountWithId(targetAccountId);

            givenWithdrawalWillFail(sourceAccount); //출금 실패
            givenDepositWillSucceed(targetAccount); //예금 성공

            //when
            SendMoneyCommand command = new SendMoneyCommand(sourceAccountId, targetAccountId, Money.of(300L));
            boolean success = sendMoneyService.sendMoney(command);

            //then
            assertThat(success).isFalse();
            then(accountLock).should().lockAccount(eq(sourceAccountId));
            then(accountLock).should().releaseAccount(eq(sourceAccountId));
            then(accountLock).should(times(0)).lockAccount(eq(targetAccountId));
        }

        @Test
        @DisplayName("송금하기 유스케이스 성공 테스트")
        void transactionSucceed() {
            //given
            Account sourceAccount = givenSourceAccount();
            Account targetAccount = givenTargetAccount();

            givenWithdrawalWithSucceed(sourceAccount);
            givenDepositWillSucceed(targetAccount);

            Money money = Money.of(500L);

            //when
            SendMoneyCommand command = new SendMoneyCommand(sourceAccount.getId().get(), targetAccount.getId().get(), money);
            boolean success = sendMoneyService.sendMoney(command);

            //then
            assertThat(success).isTrue();

            AccountId sourceAccountId = sourceAccount.getId().get();
            AccountId targetAccountId = targetAccount.getId().get();

            then(accountLock).should().lockAccount(eq(sourceAccountId));
            then(sourceAccount).should().withdraw(eq(money), eq(targetAccountId));
            then(accountLock).should().releaseAccount(eq(sourceAccountId));

            then(accountLock).should().lockAccount(eq(targetAccountId));
            then(targetAccount).should().deposit(eq(money), eq(sourceAccountId));
            then(accountLock).should().releaseAccount(eq(targetAccountId));

            thenAccountsHaveBeenUpdated(sourceAccountId, targetAccountId);

        }

        //==헬퍼 메서드==//
        private void thenAccountsHaveBeenUpdated(AccountId... accountIds) {
            ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
            then(updateAccountStatePort).should(times(accountIds.length)).updateActivities(accountCaptor.capture());

            List<AccountId> updateAccountIds = accountCaptor.getAllValues()
                .stream()
                .map(Account::getId)
                .map(Optional::get)
                .collect(Collectors.toList());

            for (AccountId accountId : accountIds) {
                assertThat(updateAccountIds).contains(accountId);
            }
        }

        private void givenDepositWillSucceed(Account account) {
            given(account.deposit(any(Money.class), any(AccountId.class))).willReturn(true);
        }

        private void givenWithdrawalWithSucceed(Account account) {
            given(account.withdraw(any(Money.class), any(AccountId.class))).willReturn(true);
        }

        private void givenWithdrawalWillFail(Account account) {
            given(account.withdraw(any(Money.class), any(AccountId.class))).willReturn(false);
        }

        private Account givenTargetAccount() {
            return givenAnAccountWithId(new AccountId(2L));
        }

        private Account givenSourceAccount() {
            return givenAnAccountWithId(new AccountId(1L));
        }

        private Account givenAnAccountWithId(AccountId accountId) {
            Account account = Mockito.mock(Account.class);
            given(account.getId()).willReturn(Optional.of(accountId));
            given(loadAccountPort.loadAccount(eq(account.getId().get()), any(LocalDateTime.class))).willReturn(account);
            return account;
        }

        private MoneyTransferProperties moneyTransferProperties() {
            return new MoneyTransferProperties(Money.of(Long.MAX_VALUE));
        }
    }

}