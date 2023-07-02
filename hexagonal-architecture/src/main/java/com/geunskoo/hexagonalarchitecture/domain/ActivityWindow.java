package com.geunskoo.hexagonalarchitecture.domain;

import com.geunskoo.hexagonalarchitecture.domain.Account.AccountId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.NonNull;

public class ActivityWindow {

    private List<Activity> activityList;

    public LocalDateTime getStartTimestamp() {
        return activityList.stream()
            .min(Comparator.comparing(Activity::getTimestamp))
            .orElseThrow(IllegalStateException::new)
            .getTimestamp();
    }

    public LocalDateTime getEndTimestamp() {
        return activityList.stream()
            .max(Comparator.comparing(Activity::getTimestamp))
            .orElseThrow(IllegalStateException::new)
            .getTimestamp();
    }

    public Money calculateBalance(AccountId accountId) {
        Money depositBalance = activityList.stream()
            .filter(a -> a.getTargetAccountId().equals(accountId))
            .map(Activity::getMoney)
            .reduce(Money.ZERO, Money::add);

        Money withdrawBalance = activityList.stream()
            .filter(a -> a.getSourceAccountId().equals(accountId))
            .map(Activity::getMoney)
            .reduce(Money.ZERO, Money::add);

        return Money.add(depositBalance, withdrawBalance.negate());
    }

    public ActivityWindow(@NonNull List<Activity> activityList) {
        this.activityList = activityList;
    }

    public ActivityWindow(@NonNull Activity... activityList) {
        this.activityList = new ArrayList<>(Arrays.asList(activityList));
    }

    public List<Activity> getActivityList() {
        return Collections.unmodifiableList(this.activityList);
    }

    public void addActivity(Activity activity) {
        this.activityList.add(activity);
    }
}
