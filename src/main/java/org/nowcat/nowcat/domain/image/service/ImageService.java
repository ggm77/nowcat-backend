package org.nowcat.nowcat.domain.image.service;

import lombok.RequiredArgsConstructor;
import org.nowcat.nowcat.domain.image.dto.ImageDto;
import org.nowcat.nowcat.domain.image.dto.ImageResponseDto;
import org.nowcat.nowcat.domain.image.entity.Image;
import org.nowcat.nowcat.domain.image.repository.ImageRepository;
import org.nowcat.nowcat.global.exception.CustomException;
import org.nowcat.nowcat.global.exception.constants.ExceptionCode;
import org.nowcat.nowcat.global.infra.storage.local.LocalFileStorage;
import org.nowcat.nowcat.global.infra.theCatApi.TheCatApiClient;
import org.nowcat.nowcat.global.utils.ImageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.servlet.autoconfigure.MultipartProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${main-image.display-duration.hours}")
    private int MAIN_IMAGE_DISPLAY_DURATION;

    @Value("${server.base-url}")
    private String SERVER_BASE_URL;

    private final MultipartProperties multipartProperties;
    private final ImageUtil imageUtil;
    private final LocalFileStorage localFileStorage;
    private final ImageRepository imageRepository;
    private final TheCatApiClient theCatApiClient;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");

    public void uploadImage(final MultipartFile file) {

        // 1) 이미지 파일 비었는지 확인
        if (file == null || file.isEmpty()) {
            throw new CustomException(ExceptionCode.EMPTY_FILE);
        }

        // 2) 이미지 파일 크기 확인 및 저장
        if (file.getSize() > multipartProperties.getMaxFileSize().toBytes()) {
            throw new CustomException(ExceptionCode.TOO_BIG_FILE);
        }
        final int fileSize = Math.toIntExact(file.getSize());

        // 3) 파일 이름 유무 검사
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            throw new CustomException(ExceptionCode.INVALID_FILE_NAME);
        }

        // 4) 확장자 존재 검사
        final String rawExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (rawExtension == null || rawExtension.isEmpty()) {
            throw new CustomException(ExceptionCode.INVALID_FILE_EXTENSION);
        }

        // 5) 지원하는 확장자인지 확인
        final String extension = rawExtension.toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new CustomException(ExceptionCode.INVALID_FILE_EXTENSION);
        }

        // 6) 파일 시그니처 검사
        try (InputStream is = file.getInputStream()) {
            final BufferedInputStream bis = new BufferedInputStream(is);
            bis.mark(16); // 여유있게 설정
            final byte[] header = bis.readNBytes(16);
            bis.reset();

            //파일 헤더만 읽어서 검사
            if (!imageUtil.isValidSignature(header, extension)) {
                throw new CustomException(ExceptionCode.INVALID_FILE);
            }
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.INVALID_FILE);
        }

        // 7) 파일명 생성 (20251227_010830_uuid.extension 형태)
        final String uuid = UUID.randomUUID().toString();
        final String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        final String fileName = timeStamp + "_" + uuid + "." + extension;

        // 8) 파일 저장
        localFileStorage.saveMultipartImage(file, fileName);

        // 9) 엔티티 빌드
        final Image image = Image.builder()
                .name(fileName)
                .extension(extension)
                .size(fileSize)
                .isConfirmed(false)
                .build();

        // 10) 이미지 정보 저장
        imageRepository.save(image);
    }

    public ImageDto getImage(final Long imageId) {

        // 1) 파일 정보 조회
        final Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ExceptionCode.FILE_INFO_NOT_FOUND));

        // 2) 확인 안된 파일이면 조회 금지
        if(!image.getIsConfirmed()){
            throw new CustomException(ExceptionCode.FILE_NOT_CONFIRMED);
        }

        // 3) 변수에 정보 저장
        final String name = image.getName();
        final String extension = image.getExtension();
        final String contentType;
        if (extension.equals("jpg") || extension.equals("jpeg")) {
            contentType = "image/jpeg";
        } else {
            contentType = "image/png";
        }

        // 4) 파일 읽어오기
        final Resource file = localFileStorage.loadImage(name);

        return ImageDto.builder()
                .file(file)
                .contentType(contentType)
                .name(name)
                .build();
    }

    public ImageResponseDto getMainImageUrl() {

        // 1) 컨펌된 이미지 중 최신 이미지 조회
        final Image image = imageRepository.findFirstByIsConfirmedTrueOrderByCreatedAtDesc()
                .orElse(null);

        // 2) 외부 API 사용해야하는지 확인 (이미지가 존재하지 않거나 오래된 이미지라면 외부 API 사용)
        final boolean useExternalImageApi = image == null
                || image.getCreatedAt().plusHours(MAIN_IMAGE_DISPLAY_DURATION).isBefore(LocalDateTime.now());

        // 3) 외부 API 사용
        if(useExternalImageApi) {
            final String url = theCatApiClient.getRandomCatImageUrl();

            //API에서 에러 발생한 경우 예외
            if(url == null)  {
                throw new CustomException(ExceptionCode.EXTERNAL_API_ERROR);
            }

            return ImageResponseDto.builder()
                    .url(url)
                    .build();
        }
        // 3) 자체 이미지 사용
        else {
            final Long imageId = image.getId();

            return ImageResponseDto.builder()
                    .url(SERVER_BASE_URL + "/api/v1/images/" + imageId)
                    .build();
        }
    }
}
