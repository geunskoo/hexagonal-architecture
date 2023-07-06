package com.geunskoo.hexagonalarchitecture.adaptor.out.persistence.mapper;

import com.geunskoo.hexagonalarchitecture.adaptor.out.persistence.entity.AccountJpaEntity;
import com.geunskoo.hexagonalarchitecture.adaptor.out.persistence.entity.ActivityJpaEntity;
import com.geunskoo.hexagonalarchitecture.domain.Account;
import com.geunskoo.hexagonalarchitecture.domain.Account.AccountId;
import com.geunskoo.hexagonalarchitecture.domain.Activity;
import com.geunskoo.hexagonalarchitecture.domain.ActivityWindow;
import com.geunskoo.hexagonalarchitecture.domain.Money;
import java.util.List;

public class AccountMapper {

    public Account mapToDomainEntity(AccountJpaEntity accountJpaEntity, List<ActivityJpaEntity> activityJpaEntities, Long withdrawalBalance,
        Long depositBalance) {

        Money baselineBalance = Money.subtract(Money.of(depositBalance), Money.of(withdrawalBalance));

        return Account.withId(new AccountId(accountJpaEntity.getId()), baselineBalance, mapToActivityWindow(activityJpaEntities));
    }

    private ActivityWindow mapToActivityWindow(List<ActivityJpaEntity> activityJpaEntities) {
        List<Activity> mappedActivities = activityJpaEntities.stream()
            .map(ActivityJpaEntity::toActivity)
            .toList();

        return new ActivityWindow(mappedActivities);
    }

    public ActivityJpaEntity mapToJpaEntity(Activity activity) {
        return new ActivityJpaEntity(
            activity.getId() == null ? null : activity.getId().getValue(),
            activity.getTimestamp(),
            activity.getOwnerAccountId().getValue(),
            activity.getSourceAccountId().getValue(),
            activity.getTargetAccountId().getValue(),
            activity.getMoney().amount().longValue()
        );
    }
}
