package org.nowcat.nowcat.domain.admin.image.controller;

import lombok.RequiredArgsConstructor;
import org.nowcat.nowcat.domain.admin.image.dto.AdminImageConfirmationRequestDto;
import org.nowcat.nowcat.domain.admin.image.dto.AdminImageListResponseDto;
import org.nowcat.nowcat.domain.admin.image.service.AdminImageService;
import org.nowcat.nowcat.domain.image.dto.ImageDto;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminImageController {

    private final AdminImageService adminImageService;

    @GetMapping("/images")
    public ResponseEntity<AdminImageListResponseDto> getImages(
            @RequestParam(required = false) final Boolean confirmed
    ) {
        return ResponseEntity.ok().body(adminImageService.getImageInfos(confirmed));
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<Resource> getImage(
            @PathVariable final Long imageId
    ) {

        final ImageDto imageDto = adminImageService.getImage(imageId);

        final HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", imageDto.getContentType());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(imageDto.getFile());
    }

    @PatchMapping("/images/{imageId}/confirmation")
    public ResponseEntity<Void> confirmImage(
            @PathVariable final Long imageId,
            @RequestBody final AdminImageConfirmationRequestDto adminImageConfirmationRequestDto
            ) {

        adminImageService.updateConfirmationImage(imageId, adminImageConfirmationRequestDto);

        return ResponseEntity.noContent().build();
    }

}
