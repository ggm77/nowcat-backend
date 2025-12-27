package org.nowcat.nowcat.domain.image.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "image",
        indexes = {
                @Index(name = "idx_is_confirmed_created_at", columnList = "is_confirmed, created_at DESC")
        }
)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) //Hibernate에서 IDENTITY를 제대로 지원 X
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 10, nullable = false)
    private String extension;

    @Column(nullable = false)
    private Integer size;

    @Column(nullable = false)
    private Boolean isConfirmed;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Image(
            final String name,
            final String extension,
            final Integer size,
            final Boolean isConfirmed
    ) {
        this.name = name;
        this.extension = extension;
        this.size = size;
        this.isConfirmed = isConfirmed;
    }

    // 이미지 컨펌 정보 업데이트하는 메서드
    public void updateIsConfirmed(final boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }
}
