package org.nowcat.nowcat.domain.image.controller;

import lombok.RequiredArgsConstructor;
import org.nowcat.nowcat.domain.image.dto.ImageDto;
import org.nowcat.nowcat.domain.image.service.ImageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ImageController {

    private final ImageService imageService;

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadImage(
            @RequestPart(value = "file") final MultipartFile multipartFile
    ) {
        final Long imageId = imageService.uploadImage(multipartFile);

        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(imageId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<Resource> getImage(
            @PathVariable final Long imageId
    ) {
        final ImageDto imageDto = imageService.getImage(imageId);

        final HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", imageDto.getContentType());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(imageDto.getFile());
    }
}
