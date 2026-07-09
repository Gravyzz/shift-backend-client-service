package ru.shift.userimporter.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.shift.userimporter.core.exception.UploadedFileNotFoundException;
import ru.shift.userimporter.core.model.ErrorCode;
import ru.shift.userimporter.core.model.FileProcessingError;
import ru.shift.userimporter.core.model.FileStatus;
import ru.shift.userimporter.core.model.UploadedFile;
import ru.shift.userimporter.core.model.User;
import ru.shift.userimporter.core.repository.FileProcessingErrorRepository;
import ru.shift.userimporter.core.repository.UploadedFileRepository;
import ru.shift.userimporter.core.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final UploadedFileRepository uploadedFileRepository;
    private final UserRepository userRepository;
    private final FileProcessingErrorRepository errorRepository;

    private static final Pattern NAME_PATTERN = Pattern.compile("^[А-ЯЁ][а-яёА-ЯЁ'\\- ]{2,49}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%-]+@(shift\\.com|shift\\.ru)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_PATTERN = Pattern.compile("^7[0-9]{10}$");

    public void ensureExists(Integer fileId) {
        if (!uploadedFileRepository.existsById(fileId)) {
            throw new UploadedFileNotFoundException("Файл не найден: " + fileId);
        }
    }

    @Async
    public void process(Integer fileId) {
        UploadedFile file = uploadedFileRepository.findById(fileId)
                .orElseThrow(() -> new UploadedFileNotFoundException("Файл не найден: " + fileId));

        file.setStatus(FileStatus.IN_PROGRESS);
        uploadedFileRepository.save(file);

        int total = 0;
        int valid = 0;
        int invalid = 0;
        int inserted = 0;
        int updated = 0;

        try {
            List<String> lines = Files.readAllLines(Paths.get(file.getStoragePath()), StandardCharsets.UTF_8);
            int rowNumber = 0;
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                rowNumber++;
                total++;
                String[] parts = line.split(",", -1);
                ErrorCode error = validate(parts);
                if (error != null) {
                    saveError(file, rowNumber, error, line);
                    invalid++;
                } else {
                    boolean isNew = upsert(parts);
                    if (isNew) {
                        inserted++;
                    } else {
                        updated++;
                    }
                    valid++;
                }
            }
            file.setStatus(FileStatus.DONE);
        } catch (Exception e) {
            file.setStatus(FileStatus.FAILED);
        }

        file.setTotalRows(total);
        file.setProcessedRows(total);
        file.setValidRows(valid);
        file.setInvalidRows(invalid);
        file.setInsertedRows(inserted);
        file.setUpdatedRows(updated);
        uploadedFileRepository.save(file);
    }

    private ErrorCode validate(String[] parts) {
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

    private boolean upsert(String[] parts) {
        String lastName = parts[0].trim();
        String firstName = parts[1].trim();
        String middleName = parts[2].trim();
        String email = parts[3].trim();
        String phone = parts[4].trim();
        LocalDate birthDate = LocalDate.parse(parts[5].trim());

        Optional<User> existing = userRepository.findByPhone(phone);
        User user = existing.orElseGet(User::new);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMiddleName(middleName.isEmpty() ? null : middleName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setBirthDate(birthDate);
        userRepository.save(user);
        return existing.isEmpty();
    }

    private void saveError(UploadedFile file, int rowNumber, ErrorCode code, String rawData) {
        FileProcessingError error = FileProcessingError.builder()
                .file(file)
                .rowNumber(rowNumber)
                .errorCode(code)
                .errorMessage(messageFor(code))
                .rawData(rawData)
                .build();
        errorRepository.save(error);
    }

    private String messageFor(ErrorCode code) {
        return switch (code) {
            case INVALID_FORMAT -> "Неверный формат строки";
            case INVALID_NAME -> "Некорректное имя";
            case INVALID_LAST_NAME -> "Некорректная фамилия";
            case INVALID_MIDDLE_NAME -> "Некорректное отчество";
            case INVALID_EMAIL -> "Некорректный email";
            case INVALID_PHONE -> "Некорректный телефон";
            case INVALID_BIRTHDATE -> "Некорректная дата рождения";
        };
    }
}
