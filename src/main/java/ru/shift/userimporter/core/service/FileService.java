package ru.shift.userimporter.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.shift.userimporter.api.dto.DetailedFileStatistic;
import ru.shift.userimporter.api.dto.FileIdResponse;
import ru.shift.userimporter.api.dto.FileResponse;
import ru.shift.userimporter.api.dto.FileStatistic;
import ru.shift.userimporter.api.dto.ProcessingError;
import ru.shift.userimporter.core.exception.DuplicateFileException;
import ru.shift.userimporter.core.exception.UploadedFileNotFoundException;
import ru.shift.userimporter.core.model.FileStatus;
import ru.shift.userimporter.core.model.UploadedFile;
import ru.shift.userimporter.core.repository.FileProcessingErrorRepository;
import ru.shift.userimporter.core.repository.UploadedFileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final UploadedFileRepository uploadedFileRepository;
    private final FileProcessingErrorRepository errorRepository;

    @Value("${app.storage-dir}")
    private String storageDir;

    public FileIdResponse uploadFile(MultipartFile file) {
        byte[] bytes = readBytes(file);
        String hash = sha256(bytes);

        if (uploadedFileRepository.findByHash(hash).isPresent()) {
            throw new DuplicateFileException("Файл уже загружен");
        }

        String originalFilename = file.getOriginalFilename();
        String storagePath = saveToDisk(bytes, hash, originalFilename);

        UploadedFile uploadedFile = UploadedFile.builder()
                .originalFilename(originalFilename)
                .storagePath(storagePath)
                .hash(hash)
                .status(FileStatus.NEW)
                .build();

        UploadedFile saved = uploadedFileRepository.save(uploadedFile);
        return new FileIdResponse(saved.getId().toString());
    }

    public List<FileResponse> getFiles(String status) {
        List<UploadedFile> files;
        if (status == null) {
            files = uploadedFileRepository.findAll();
        } else {
            files = uploadedFileRepository.findByStatus(FileStatus.valueOf(status));
        }
        return files.stream()
                .map(this::toFileResponse)
                .toList();
    }

    public DetailedFileStatistic getDetailedStatistic(Integer fileId) {
        UploadedFile file = uploadedFileRepository.findById(fileId)
                .orElseThrow(() -> new UploadedFileNotFoundException("Файл не найден: " + fileId));

        List<ProcessingError> errors = errorRepository.findByFileId(fileId).stream()
                .map(e -> new ProcessingError(e.getRowNumber(), e.getErrorCode().name(), e.getErrorMessage()))
                .toList();

        return new DetailedFileStatistic(file.getInsertedRows(), file.getUpdatedRows(), errors);
    }

    private FileResponse toFileResponse(UploadedFile file) {
        FileStatistic statistic = new FileStatistic(file.getInsertedRows(), file.getUpdatedRows(), file.getInvalidRows());
        return new FileResponse(file.getId().toString(), file.getStatus().name(), statistic);
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл", e);
        }
    }

    private String saveToDisk(byte[] bytes, String hash, String originalFilename) {
        try {
            Path dir = Paths.get(storageDir);
            Files.createDirectories(dir);
            Path target = dir.resolve(hash + "_" + originalFilename);
            Files.write(target, bytes);
            return target.toString();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл", e);
        }
    }

    private String sha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
