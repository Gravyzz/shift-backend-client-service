package ru.shift.userimporter.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.shift.userimporter.core.exception.ApiErrorType;
import ru.shift.userimporter.core.exception.ApiException;
import ru.shift.userimporter.core.model.FileStatus;
import ru.shift.userimporter.core.model.UploadedFile;
import ru.shift.userimporter.core.repository.UploadedFileRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UploadedFileService {

    private final UploadedFileRepository uploadedFileRepository;

    public UploadedFile getById(Integer id) {
        return uploadedFileRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiErrorType.FILE_NOT_FOUND));
    }

    public boolean existsById(Integer id) {
        return uploadedFileRepository.existsById(id);
    }

    public UploadedFile save(UploadedFile file) {
        return uploadedFileRepository.save(file);
    }

    public List<UploadedFile> findAll() {
        return uploadedFileRepository.findAll();
    }

    public List<UploadedFile> findByStatus(FileStatus status) {
        return uploadedFileRepository.findByStatus(status);
    }

    public Optional<UploadedFile> findByHash(String hash) {
        return uploadedFileRepository.findByHash(hash);
    }
}
