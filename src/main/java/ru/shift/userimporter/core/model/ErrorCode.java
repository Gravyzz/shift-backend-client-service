package ru.shift.userimporter.core.model;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_FORMAT("Неверный формат строки"),
    INVALID_NAME("Некорректное имя"),
    INVALID_LAST_NAME("Некорректная фамилия"),
    INVALID_MIDDLE_NAME("Некорректное отчество"),
    INVALID_EMAIL("Некорректный email"),
    INVALID_PHONE("Некорректный телефон"),
    INVALID_BIRTHDATE("Некорректная дата рождения");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
