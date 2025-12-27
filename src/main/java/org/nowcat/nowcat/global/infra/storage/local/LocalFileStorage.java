package org.nowcat.nowcat.global.infra.storage.local;

import org.nowcat.nowcat.global.exception.CustomException;
import org.nowcat.nowcat.global.exception.constants.ExceptionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    /**
     * 로컬에서 이미지를 읽어오는 메서드
     * @param fileName 읽어올 파일의 이름
     * @return Resource 이미지 파일 리소스
     */
    public Resource loadImage(final String fileName) {
        try {
            // 1) 경로 조합
            final Path path = Paths.get(IMAGE_DIR).resolve(fileName).normalize();

            // 2) 다른 경로로 탈출 방지
            if(!path.startsWith(IMAGE_DIR)){
                throw new CustomException(ExceptionCode.INVALID_FILE_PATH);
            }

            // 3) 파일 읽고 리턴
            final Resource resource = new UrlResource(path.toUri());

            //파일이 없거나 파일을 읽을 수 없으면 예외
            if(!resource.exists() || !resource.isReadable()){
                throw new CustomException(ExceptionCode.INVALID_FILE_PATH);
            }

            return resource;

        } catch (MalformedURLException ex){
            throw new CustomException(ExceptionCode.INVALID_FILE_PATH);
        }
    }
}
