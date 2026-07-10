package ru.shift.userimporter.core.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ApiErrorType type;

    public ApiException(ApiErrorType type) {
        super(type.getMessage());
        this.type = type;
    }
}
