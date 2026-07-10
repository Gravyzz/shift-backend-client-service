package ru.shift.userimporter.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.shift.userimporter.core.model.FileStatus;
import ru.shift.userimporter.core.model.UploadedFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final UploadedFileService uploadedFileService;
    private final FileImportProcessor fileImportProcessor;

    public void ensureExists(Integer fileId) {
        uploadedFileService.getById(fileId);
    }

    @Async
    public void process(Integer fileId) {
        UploadedFile file = uploadedFileService.getById(fileId);
        file.setStatus(FileStatus.IN_PROGRESS);
        uploadedFileService.save(file);

        try {
            ProcessingResult result = fileImportProcessor.importFile(file);
            file.setTotalRows(result.total());
            file.setProcessedRows(result.total());
            file.setValidRows(result.valid());
            file.setInvalidRows(result.invalid());
            file.setInsertedRows(result.inserted());
            file.setUpdatedRows(result.updated());
            file.setStatus(FileStatus.DONE);
        } catch (Exception e) {
            log.error("Не удалось обработать файл {}", fileId, e);
            file.setStatus(FileStatus.FAILED);
        }

        uploadedFileService.save(file);
    }
}
