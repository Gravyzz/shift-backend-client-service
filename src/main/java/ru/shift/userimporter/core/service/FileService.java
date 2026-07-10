package ru.shift.userimporter.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.shift.userimporter.api.dto.FileIdResponse;
import ru.shift.userimporter.core.exception.ApiErrorType;
import ru.shift.userimporter.core.exception.ApiException;
import ru.shift.userimporter.core.model.FileStatus;
import ru.shift.userimporter.core.model.UploadedFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileService {

    private final UploadedFileService uploadedFileService;
    private final HashService hashService;
    private final FileStorageService fileStorageService;

    public FileIdResponse uploadFile(MultipartFile file) {
        byte[] bytes = readBytes(file);
        String hash = hashService.sha256(bytes);

        if (uploadedFileService.findByHash(hash).isPresent()) {
            throw new ApiException(ApiErrorType.DUPLICATE_FILE);
        }

        String originalFilename = file.getOriginalFilename();
        String storagePath = fileStorageService.store(bytes, hash + "_" + originalFilename);

        UploadedFile uploadedFile = UploadedFile.builder()
                .originalFilename(originalFilename)
                .storagePath(storagePath)
                .hash(hash)
                .status(FileStatus.NEW)
                .build();

        return new FileIdResponse(uploadedFileService.save(uploadedFile).getId().toString());
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл", e);
        }
    }
}
