package com.geunskoo.hexagonalarchitecture.application.service;

import com.geunskoo.hexagonalarchitecture.application.port.in.SendMoneyCommand;
import com.geunskoo.hexagonalarchitecture.application.port.in.SendMoneyUseCase;
import com.geunskoo.hexagonalarchitecture.application.port.out.AccountLock;
import com.geunskoo.hexagonalarchitecture.application.port.out.LoadAccountPort;
import com.geunskoo.hexagonalarchitecture.application.port.out.UpdateAccountStatePort;
import com.geunskoo.hexagonalarchitecture.domain.Account;
import com.geunskoo.hexagonalarchitecture.domain.Account.AccountId;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SendMoneyService implements SendMoneyUseCase {

    private final LoadAccountPort loadAccountPort;
    private final AccountLock accountLock;
    private final UpdateAccountStatePort updateAccountStatePort;
    private final MoneyTransferProperties moneyTransferProperties;

    /**
     * SendMoney : 상대 계좌로 송금하기
     * 상      세 : 내 계좌로부터 인출(withdraw)해서 상대 계좌로 입금(deposit)하는 Flow이다.
     */

    @Override
    public boolean sendMoney(SendMoneyCommand command) {

        checkThreshold(command);

        LocalDateTime baselineTime = LocalDateTime.now().minusDays(10);

        Account sourceAccount = loadAccountPort.loadAccount(command.getSourceAccountId(), baselineTime);
        Account targetAccount = loadAccountPort.loadAccount(command.getTargetAccountId(), baselineTime);

        AccountId sourceAccountId = sourceAccount.getId()
            .orElseThrow(() -> new IllegalStateException("expected source account ID not to be empty"));
        AccountId targetAccountId = targetAccount.getId()
            .orElseThrow(() -> new IllegalStateException("expected target account ID not to be empty"));

        accountLock.lockAccount(sourceAccountId);
        if (!sourceAccount.withdraw(command.getMoney(), targetAccountId)) {
            accountLock.releaseAccount(sourceAccountId);
            return false;
        }

        accountLock.lockAccount(targetAccountId);
        if (!targetAccount.deposit(command.getMoney(), sourceAccountId)) {
            accountLock.releaseAccount(sourceAccountId);
            accountLock.releaseAccount(targetAccountId);
            return false;
        }

        updateAccountStatePort.updateActivities(sourceAccount);
        updateAccountStatePort.updateActivities(targetAccount);

        accountLock.releaseAccount(sourceAccountId);
        accountLock.releaseAccount(targetAccountId);
        return true;
    }

    private void checkThreshold(SendMoneyCommand command) {
        if (command.getMoney().isGreaterThanOrEqualTo(moneyTransferProperties.getMaximumTransferThreshold())) {
            throw new IllegalStateException("거래 금액이 100만원이 이상 초과되었습니다");
        }
    }
}
