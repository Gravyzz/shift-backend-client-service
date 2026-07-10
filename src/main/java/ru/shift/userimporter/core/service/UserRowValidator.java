package ru.shift.userimporter.core.service;

import org.springframework.stereotype.Component;
import ru.shift.userimporter.core.model.ErrorCode;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

@Component
public class UserRowValidator {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[А-ЯЁ][а-яёА-ЯЁ'\\- ]{2,49}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%-]+@(shift\\.com|shift\\.ru)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_PATTERN = Pattern.compile("^7[0-9]{10}$");

    public ErrorCode validate(String[] parts) {
        if (parts.length != 6) {
            return ErrorCode.INVALID_FORMAT;
        }
        String lastName = parts[0].trim();
        String firstName = parts[1].trim();
        String middleName = parts[2].trim();
        String email = parts[3].trim();
        String phone = parts[4].trim();
        String birthDate = parts[5].trim();

        if (!isValidName(lastName)) {
            return ErrorCode.INVALID_LAST_NAME;
        }
        if (!isValidName(firstName)) {
            return ErrorCode.INVALID_NAME;
        }
        if (!middleName.isEmpty() && !isValidName(middleName)) {
            return ErrorCode.INVALID_MIDDLE_NAME;
        }
        if (!isValidEmail(email)) {
            return ErrorCode.INVALID_EMAIL;
        }
        if (!isValidPhone(phone)) {
            return ErrorCode.INVALID_PHONE;
        }
        if (!isValidBirthDate(birthDate)) {
            return ErrorCode.INVALID_BIRTHDATE;
        }
        return null;
    }

    private boolean isValidName(String value) {
        return value.length() >= 3 && value.length() <= 50 && NAME_PATTERN.matcher(value).matches();
    }

    private boolean isValidEmail(String value) {
        return value.length() <= 100 && EMAIL_PATTERN.matcher(value).matches();
    }

    private boolean isValidPhone(String value) {
        return PHONE_PATTERN.matcher(value).matches();
    }

    private boolean isValidBirthDate(String value) {
        try {
            LocalDate date = LocalDate.parse(value);
            return !date.isAfter(LocalDate.now().minusYears(18));
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
