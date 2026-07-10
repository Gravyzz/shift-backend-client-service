package ru.shift.userimporter.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.shift.userimporter.api.dto.DetailedFileStatistic;
import ru.shift.userimporter.api.dto.FileIdResponse;
import ru.shift.userimporter.api.dto.FileResponse;
import ru.shift.userimporter.core.service.FileProcessingService;
import ru.shift.userimporter.core.service.FileService;
import ru.shift.userimporter.core.service.FileStatisticsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileProcessingService fileProcessingService;
    private final FileStatisticsService fileStatisticsService;

    @PostMapping("/files")
    @ResponseStatus(HttpStatus.CREATED)
    public FileIdResponse sendFile(@RequestParam("file") MultipartFile file) {
        return fileService.uploadFile(file);
    }

    @PostMapping("/files/{fileId}/processing")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void processing(@PathVariable String fileId) {
        Integer id = Integer.valueOf(fileId);
        fileProcessingService.ensureExists(id);
        fileProcessingService.process(id);
    }

    @GetMapping("/files/statistics")
    public List<FileResponse> getFileStatistic(@RequestParam(required = false) String status) {
        return fileStatisticsService.getFiles(status);
    }

    @GetMapping("/files/{fileId}/statistics")
    public DetailedFileStatistic getDetailedFileStatistic(@PathVariable String fileId) {
        return fileStatisticsService.getDetailedStatistic(Integer.valueOf(fileId));
    }
}
