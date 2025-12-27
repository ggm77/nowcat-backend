package org.nowcat.nowcat.global.exception;

import lombok.Getter;
import org.nowcat.nowcat.global.exception.constants.ExceptionCode;

@Getter
public class CustomException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public CustomException(final ExceptionCode exceptionCode) {
        super("");
        this.exceptionCode = exceptionCode;
    }
}
