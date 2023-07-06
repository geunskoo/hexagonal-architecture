package com.geunskoo.hexagonalarchitecture.adaptor.out.persistence;

import com.geunskoo.hexagonalarchitecture.adaptor.out.persistence.entity.AccountJpaEntity;
import com.geunskoo.hexagonalarchitecture.adaptor.out.persistence.entity.ActivityJpaEntity;
import com.geunskoo.hexagonalarchitecture.adaptor.out.persistence.mapper.AccountMapper;
import com.geunskoo.hexagonalarchitecture.adaptor.out.persistence.repository.AccountRepository;
import com.geunskoo.hexagonalarchitecture.adaptor.out.persistence.repository.ActivityRepository;
import com.geunskoo.hexagonalarchitecture.application.port.out.LoadAccountPort;
import com.geunskoo.hexagonalarchitecture.application.port.out.UpdateAccountStatePort;
import com.geunskoo.hexagonalarchitecture.domain.Account;
import com.geunskoo.hexagonalarchitecture.domain.Account.AccountId;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountPersistenceAdaptor implements LoadAccountPort, UpdateAccountStatePort {

    private final AccountRepository accountRepository;
    private final ActivityRepository activityRepository;
    private final AccountMapper accountMapper;


    @Override
    public Account loadAccount(AccountId accountId, LocalDateTime baselineDate) {
        AccountJpaEntity accountJpaEntity = accountRepository.findById(accountId.getValue())
            .orElseThrow(EntityNotFoundException::new);

        List<ActivityJpaEntity> activityJpaEntities = activityRepository.findByOwnerSince(accountId.getValue(), baselineDate);

        Long withdrawalBalance = orZero(activityRepository.getWithdrawalBalanceUntil(accountId.getValue(), baselineDate));
        Long depositBalance = orZero(activityRepository.getDepositBalanceUntil(accountId.getValue(), baselineDate));

        return accountMapper.mapToDomainEntity(accountJpaEntity, activityJpaEntities, withdrawalBalance, depositBalance);
    }

    private Long orZero(Long value) {
        return value == null ? 0L : value;
    }

    @Override
    public void updateActivities(Account account) {
        account.getActivityWindow().getActivityList().stream()
            .filter(a -> a.getId() == null)
            .map(activity -> accountMapper.mapToJpaEntity(activity))
            .forEach(activityJpaEntity -> activityRepository.save(activityJpaEntity));
    }
}
