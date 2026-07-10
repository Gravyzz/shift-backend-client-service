package ru.shift.userimporter.api.dto;

import java.time.Instant;
import java.time.LocalDate;

public record ClientResponse(
        long phone,
        String name,
        String lastName,
        String middleName,
        String email,
        LocalDate birthdate,
        Instant creationTime,
        Instant updateTime
) {
}
