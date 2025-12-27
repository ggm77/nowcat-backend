package org.nowcat.nowcat.domain.admin.image.dto;

import lombok.Getter;
import org.nowcat.nowcat.domain.image.entity.Image;

import java.time.LocalDateTime;

@Getter
public class AdminImageResponseDto {
    private final Long id;
    private final String name;
    private final boolean isConfirmed;
    private final LocalDateTime createdAt;

    public AdminImageResponseDto(
            final Image image
    ) {
        this.id = image.getId();
        this.name = image.getName();
        this.isConfirmed = image.getIsConfirmed();
        this.createdAt = image.getCreatedAt();
    }
}
