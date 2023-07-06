package com.geunskoo.hexagonalarchitecture.adaptor.out.persistence.entity;

import com.geunskoo.hexagonalarchitecture.domain.Account;
import com.geunskoo.hexagonalarchitecture.domain.Account.AccountId;
import com.geunskoo.hexagonalarchitecture.domain.Activity;
import com.geunskoo.hexagonalarchitecture.domain.Activity.ActivityId;
import com.geunskoo.hexagonalarchitecture.domain.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "activity")
@AllArgsConstructor
@NoArgsConstructor
public class ActivityJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime timestamp;

    @Column
    private Long ownerAccountId;

    @Column
    private Long sourceAccountId;

    @Column
    private Long targetAccountId;

    @Column
    private Long amount;

    public static Activity toActivity(ActivityJpaEntity activity) {
        return new Activity(new ActivityId(activity.getId()),
            new AccountId(activity.getOwnerAccountId()),
            new AccountId(activity.getSourceAccountId()),
            new AccountId(activity.getTargetAccountId()),
            activity.getTimestamp(),
            Money.of(activity.getAmount()));
    }

}
