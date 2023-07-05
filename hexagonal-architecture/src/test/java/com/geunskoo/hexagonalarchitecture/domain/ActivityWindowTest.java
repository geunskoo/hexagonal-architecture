package com.geunskoo.hexagonalarchitecture.domain;

import static com.geunskoo.common.ActivityTestData.defaultActivity;
import static org.assertj.core.api.Assertions.*;

import com.geunskoo.hexagonalarchitecture.domain.Account.AccountId;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ActivityWindow subDomain TEST")
class ActivityWindowTest {

    @Nested
    @DisplayName("거래내역 금액 조회 테스트")
    class calculateBalanceTest {

        @Test
        @DisplayName("거래내역 조회 성공")
        void calculateBalance() {
            AccountId account1 = new AccountId(1L);
            AccountId account2 = new AccountId(2L);

            ActivityWindow window = new ActivityWindow(
                defaultActivity()
                    .withSourceAccount(account1)
                    .withTargetAccount(account2)
                    .withMoney(Money.of(999)).build(),
                defaultActivity()
                    .withSourceAccount(account1)
                    .withTargetAccount(account2)
                    .withMoney(Money.of(1)).build(),
                defaultActivity()
                    .withSourceAccount(account2)
                    .withTargetAccount(account1)
                    .withMoney(Money.of(500)).build());

            //then
            assertThat(window.calculateBalance(account1)).isEqualTo(Money.of(-500));
            assertThat(window.calculateBalance(account2)).isEqualTo(Money.of(500));
        }
    }

    @Nested
    @DisplayName("거래내역 시간 테스트")
    class TimeStampTest {

        @Test
        @DisplayName("과거 거래 시간")
        void calculateStartTimestamp() {
            ActivityWindow activityWindow = new ActivityWindow(
                defaultActivity().withTimestamp(startDate()).build(),
                defaultActivity().withTimestamp(inBetweenDate()).build(),
                defaultActivity().withTimestamp(endDate()).build()
            );

            assertThat(activityWindow.getStartTimestamp()).isEqualTo(startDate());
        }

        @Test
        @DisplayName("최신 거래 시간")
        void calculateEndTimestamp() {
            ActivityWindow activityWindow = new ActivityWindow(
                defaultActivity().withTimestamp(startDate()).build(),
                defaultActivity().withTimestamp(inBetweenDate()).build(),
                defaultActivity().withTimestamp(endDate()).build()
            );

            assertThat(activityWindow.getEndTimestamp()).isEqualTo(endDate());
        }
    }

    private LocalDateTime startDate() {
        return LocalDateTime.of(2023, 7, 1, 0, 0);
    }

    private LocalDateTime inBetweenDate() {
        return LocalDateTime.of(2023, 7, 2, 0, 0);
    }

    private LocalDateTime endDate() {
        return LocalDateTime.of(2023, 7, 3, 0, 0);
    }
}