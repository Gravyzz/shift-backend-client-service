package ru.shift.userimporter.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shift.userimporter.core.model.ErrorCode;
import ru.shift.userimporter.core.model.FileProcessingError;
import ru.shift.userimporter.core.model.UploadedFile;
import ru.shift.userimporter.core.model.User;
import ru.shift.userimporter.core.repository.FileProcessingErrorRepository;
import ru.shift.userimporter.core.repository.UserRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FileImportProcessor {

    private final UserRepository userRepository;
    private final FileProcessingErrorRepository errorRepository;
    private final UserRowValidator userRowValidator;

    @Transactional
    public ProcessingResult importFile(UploadedFile file) {
        int total = 0;
        int valid = 0;
        int invalid = 0;
        int inserted = 0;
        int updated = 0;
        List<FileProcessingError> errors = new ArrayList<>();

        try (Stream<String> lines = Files.lines(Paths.get(file.getStoragePath()), StandardCharsets.UTF_8)) {
            Iterator<String> iterator = lines.iterator();
            int rowNumber = 0;
            while (iterator.hasNext()) {
                String line = iterator.next();
                if (line.isBlank()) {
                    continue;
                }
                rowNumber++;
                total++;
                String[] parts = line.split(",", -1);
                ErrorCode error = userRowValidator.validate(parts);
                if (error != null) {
                    errors.add(buildError(file, rowNumber, error, line));
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
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        errorRepository.saveAll(errors);
        return new ProcessingResult(total, valid, invalid, inserted, updated);
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

    private FileProcessingError buildError(UploadedFile file, int rowNumber, ErrorCode code, String rawData) {
        return FileProcessingError.builder()
                .file(file)
                .rowNumber(rowNumber)
                .errorCode(code)
                .errorMessage(code.getMessage())
                .rawData(rawData)
                .build();
    }
}
