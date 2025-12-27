package org.nowcat.nowcat.global.exception.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    EMPTY_FILE(HttpStatus.BAD_REQUEST, "파일이 비어있습니다."),
    TOO_BIG_FILE(HttpStatus.BAD_REQUEST, "파일의 크기가 너무 큽니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "파일 이름이 없습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "파일 확장자가 올바르지 않습니다."),
    INVALID_FILE(HttpStatus.BAD_REQUEST, "올바르지 않은 파일입니다."),
    FILE_INFO_NOT_FOUND(HttpStatus.BAD_REQUEST, "파일을 정보를 찾지 못했습니다."),

    FILE_NOT_CONFIRMED(HttpStatus.FORBIDDEN, "확인 되지 않은 파일입니다."),
    INVALID_FILE_PATH(HttpStatus.FORBIDDEN, "올바르지 않은 경로입니다."),

    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "외부 API와 통신 중 에러가 발생했습니다."),
    PARENT_FOLDER_NOT_EXIST(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 저장할 폴더가 없습니다."),
    FILE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 에러가 발생했습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
