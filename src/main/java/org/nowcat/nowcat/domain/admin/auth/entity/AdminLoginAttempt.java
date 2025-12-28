package org.nowcat.nowcat.domain.admin.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminLoginAttempt {
    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private Integer attempts;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public AdminLoginAttempt(final Integer initialAttempts) {
        this.id = 1L;
        this.attempts = initialAttempts;
    }

    //시도 횟수 1증가
    public void addAttempt() {
        this.attempts++;
    }

    //시도 횟수 초기화용 업데이트 메서드
    public void updateAttempt(final Integer attempts) {
        this.attempts = attempts;
    }
}
