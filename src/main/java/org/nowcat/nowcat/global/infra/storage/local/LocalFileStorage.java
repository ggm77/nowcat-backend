package org.nowcat.nowcat.global.infra.storage.local;

import org.nowcat.nowcat.global.exception.CustomException;
import org.nowcat.nowcat.global.exception.constants.ExceptionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
public class LocalFileStorage {

    @Value("${file.image.dir}")
    private String IMAGE_DIR;

    /**
     * 로컬에 파일을 MultipartFile 형시의 이미지를 저장하는 메서드
     * @param multipartFile 저장할 이미지 파일
     * @param fileName 저장할 파일 이름
     */
    public void saveMultipartImage(
            final MultipartFile multipartFile,
            final String fileName
    ) {

        // 1) 파일 절대경로 생성
        final String filePath = IMAGE_DIR + "/" + fileName;

        // 2) 파일 저장
        final File file = new File(filePath);
        try {
            //부모 폴더 존재 확인
            final File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                throw new CustomException(ExceptionCode.PARENT_FOLDER_NOT_EXIST);
            }

            //파일 저장
            multipartFile.transferTo(file);

        } catch (final IOException ex) {
            throw new  CustomException(ExceptionCode.FILE_SAVE_FAILED);
        }
    }
}
