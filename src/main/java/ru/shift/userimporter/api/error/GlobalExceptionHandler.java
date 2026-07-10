package ru.shift.userimporter.api.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.shift.userimporter.api.dto.Error;
import ru.shift.userimporter.core.exception.ApiErrorType;
import ru.shift.userimporter.core.exception.ApiException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Error> handleApi(ApiException e) {
        return ResponseEntity.status(e.getType().getStatus()).body(new Error(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleBadRequest(IllegalArgumentException e) {
        log.warn("Некорректный запрос", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error(ApiErrorType.BAD_REQUEST.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleOther(Exception e) {
        log.error("Непредвиденная ошибка", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Error(ApiErrorType.INTERNAL.getMessage()));
    }
}
