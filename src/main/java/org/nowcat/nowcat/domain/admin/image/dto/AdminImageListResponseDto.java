package org.nowcat.nowcat.domain.admin.image.dto;

import lombok.Builder;
import lombok.Getter;
import org.nowcat.nowcat.domain.image.entity.Image;

import java.util.List;

@Getter
public class AdminImageListResponseDto {
    private final List<AdminImageResponseDto> images;

    @Builder
    public AdminImageListResponseDto(
            final List<Image> images
    ) {
        this.images = images.stream()
                .map(AdminImageResponseDto::new)
                .toList();
    }
}
