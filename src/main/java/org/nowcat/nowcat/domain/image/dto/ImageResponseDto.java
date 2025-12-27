package org.nowcat.nowcat.domain.image.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ImageResponseDto {
    private final String url;

    @Builder
    public ImageResponseDto(final String url) {
        this.url = url;
    }
}
