package org.nowcat.nowcat.domain.admin.image.service;

import lombok.RequiredArgsConstructor;
import org.nowcat.nowcat.domain.admin.image.dto.AdminImageConfirmationRequestDto;
import org.nowcat.nowcat.domain.admin.image.dto.AdminImageListResponseDto;
import org.nowcat.nowcat.domain.image.dto.ImageDto;
import org.nowcat.nowcat.domain.image.entity.Image;
import org.nowcat.nowcat.domain.image.repository.ImageRepository;
import org.nowcat.nowcat.global.exception.CustomException;
import org.nowcat.nowcat.global.exception.constants.ExceptionCode;
import org.nowcat.nowcat.global.infra.storage.local.LocalFileStorage;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminImageService {

    private final ImageRepository imageRepository;
    private final LocalFileStorage localFileStorage;

    public AdminImageListResponseDto getImageInfos(final Boolean confirmed) {

        // confirmed 값에 따라서 이미지 정보 조회
        final List<Image> images;
        //최신순으로 조회
        if (confirmed == null) {
            images = imageRepository.findAllByOrderByCreatedAtDesc();
        }
        //confirm=true면 컨펌 된 이미지만, 아니면 컨펌 안된 이미지만
        else {
            images = imageRepository.findAllByIsConfirmedOrderByCreatedAtDesc(confirmed);
        }

        return AdminImageListResponseDto.builder()
                .images(images)
                .build();
    }

    public ImageDto getImage(final Long imageId) {

        // 1) 파일 정보 조회
        final Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ExceptionCode.FILE_INFO_NOT_FOUND));

        // 2) 변수에 정보 저장
        final String name = image.getName();
        final String extension = image.getExtension();
        final String contentType;
        if (extension.equals("jpg") || extension.equals("jpeg")) {
            contentType = "image/jpeg";
        } else {
            contentType = "image/png";
        }

        // 3) 파일 읽어오기
        final Resource file = localFileStorage.loadImage(name);

        return ImageDto.builder()
                .file(file)
                .contentType(contentType)
                .name(name)
                .build();
    }

    @Transactional
    public void updateConfirmationImage(
            final Long imageId,
            final AdminImageConfirmationRequestDto adminImageConfirmationRequestDto
    ) {

        // 1) 이미지 조회
        final Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ExceptionCode.FILE_INFO_NOT_FOUND));

        // 2) 이미지 컨펌
        image.updateIsConfirmed(adminImageConfirmationRequestDto.getIsConfirmed());
    }


}
