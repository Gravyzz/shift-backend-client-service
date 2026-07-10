package ru.shift.userimporter.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.shift.userimporter.api.dto.DetailedFileStatistic;
import ru.shift.userimporter.api.dto.FileResponse;
import ru.shift.userimporter.api.dto.FileStatistic;
import ru.shift.userimporter.api.dto.ProcessingError;
import ru.shift.userimporter.core.model.FileStatus;
import ru.shift.userimporter.core.model.UploadedFile;
import ru.shift.userimporter.core.repository.FileProcessingErrorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStatisticsService {

    private final UploadedFileService uploadedFileService;
    private final FileProcessingErrorRepository errorRepository;

    public List<FileResponse> getFiles(String status) {
        List<UploadedFile> files = status == null
                ? uploadedFileService.findAll()
                : uploadedFileService.findByStatus(FileStatus.valueOf(status));
        return files.stream()
                .map(this::toFileResponse)
                .toList();
    }

    public DetailedFileStatistic getDetailedStatistic(Integer fileId) {
        UploadedFile file = uploadedFileService.getById(fileId);

        List<ProcessingError> errors = errorRepository.findByFileId(fileId).stream()
                .map(e -> new ProcessingError(e.getRowNumber(), e.getErrorCode().name(), e.getErrorMessage()))
                .toList();

        return new DetailedFileStatistic(file.getInsertedRows(), file.getUpdatedRows(), errors);
    }

    private FileResponse toFileResponse(UploadedFile file) {
        FileStatistic statistic = new FileStatistic(file.getInsertedRows(), file.getUpdatedRows(), file.getInvalidRows());
        return new FileResponse(file.getId().toString(), file.getStatus().name(), statistic);
    }
}
