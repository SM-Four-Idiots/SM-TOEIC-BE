package com.sm_four_idiot.backend.domain;

import com.sm_four_idiot.backend.config.TierConfig;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * 사용자 엔티티
 * - 일반 사용자(USER)와 관리자(ADMIN) 권한을 구분
 * - 이메일을 고유 식별자로 사용
 */

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class User {

    /** 사용자 고유 ID (auto increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 로그인 이메일 (중복 불가) */
    @Column(nullable = false, unique = true)
    private String email;

    /** BCrypt 암호화된 비밀번호 */
    @Column(nullable = false)
    private String password;

    /** 사용자 닉네임 */
    @Column(nullable = false)
    private String nickname;

    /** 권한: USER(일반 사용자), ADMIN(관리자) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** 현재 보유 경험치 (PBI-6) */
    @Column(nullable = false)
    @Builder.Default
    private int xp = 0;

    /** 현재 티어 (PBI-6.1) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Tier tier = Tier.BRONZE;

    /** 승급 테스트 응시 자격 여부 (PBI-8) */
    @Column(nullable = false)
    @Builder.Default
    private boolean tierUpgradeEligible = false;

    /** 계정 생성 일시 (수정 불가) */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 승급 테스트 응시 날짜 */
    @Column
    private LocalDate tierUpgradeAttemptDate;

    /** 오늘 응시 횟수 (최대 3회) */
    @Column(nullable = false)
    @Builder.Default
    private int tierUpgradeAttemptCount = 0;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum Role {
        USER, ADMIN
    }

    /** XP 추가 후 승급 자격 자동 체크 */
    public void addXp(int amount) {
        this.xp += amount;
        this.tierUpgradeEligible = this.xp >= TierConfig.getRequiredXp(this.tier);
    }

    /** 티어 승급 */
    public void upgradeTier() {
        this.tier = TierConfig.nextTier(this.tier);
        this.xp = 0;
        this.tierUpgradeEligible = false;
        this.tierUpgradeAttemptDate = null;  // 추가
        this.tierUpgradeAttemptCount = 0;   // 추가
    }

    /** 승급 테스트 응시 횟수 증가 (날짜 다르면 초기화) */
    public void incrementTierUpgradeAttempt() {
        if (tierUpgradeAttemptDate == null || !tierUpgradeAttemptDate.equals(LocalDate.now())) {
            this.tierUpgradeAttemptDate = LocalDate.now();
            this.tierUpgradeAttemptCount = 0;
        }
        this.tierUpgradeAttemptCount++;
    }

    /** 오늘 응시 가능 여부 확인 */
    public boolean canAttemptTierUpgrade() {
        if (tierUpgradeAttemptDate == null || !tierUpgradeAttemptDate.equals(LocalDate.now())) {
            return true;
        }
        return this.tierUpgradeAttemptCount < TierConfig.TIER_UPGRADE_ATTEMPT_LIMIT;
    }

    /** 오늘 3회 모두 실패했는지 확인 (PBI-11 오답 노트용) */
    public boolean isAllAttemptsFailedToday() {
        return tierUpgradeAttemptDate != null
                && tierUpgradeAttemptDate.equals(LocalDate.now())
                && tierUpgradeAttemptCount >= TierConfig.TIER_UPGRADE_ATTEMPT_LIMIT;
    }
}