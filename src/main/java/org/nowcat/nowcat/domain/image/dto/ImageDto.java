package org.nowcat.nowcat.domain.image.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
public class ImageDto {
    private final Resource file;
    private final String contentType;
    private final String name;

    @Builder
    public ImageDto(
            final Resource file,
            final String contentType,
            final String name
    ) {
        this.file = file;
        this.contentType = contentType;
        this.name = name;
    }
}
