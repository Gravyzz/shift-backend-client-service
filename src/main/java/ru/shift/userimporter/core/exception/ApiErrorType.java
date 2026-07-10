package ru.shift.userimporter.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiErrorType {

    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "Файл не найден"),
    DUPLICATE_FILE(HttpStatus.CONFLICT, "Файл уже загружен"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Некорректный запрос"),
    INTERNAL(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");

    private final HttpStatus status;
    private final String message;

    ApiErrorType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
