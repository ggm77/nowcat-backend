package org.nowcat.nowcat.global.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.nowcat.nowcat.global.exception.CustomException;
import org.nowcat.nowcat.global.exception.constants.ExceptionCode;
import org.nowcat.nowcat.global.exception.response.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleException(final CustomException ex) {

        final ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getExceptionCode());

        return ResponseEntity.status(ex.getExceptionCode().getHttpStatus()).body(exceptionResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleALLException(final Exception ex) {

        log.error("INTERNAL_SERVER_ERROR: {}", ex.getMessage(), ex);

        final ExceptionResponse exceptionResponse = new ExceptionResponse(ExceptionCode.INTERNAL_SERVER_ERROR);

        return ResponseEntity.internalServerError().body(exceptionResponse);
    }
}
