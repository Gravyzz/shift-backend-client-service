package ru.shift.userimporter.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.shift.userimporter.api.dto.DetailedFileStatistic;
import ru.shift.userimporter.api.dto.FileIdResponse;
import ru.shift.userimporter.api.dto.FileResponse;
import ru.shift.userimporter.core.service.FileProcessingService;
import ru.shift.userimporter.core.service.FileService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileProcessingService fileProcessingService;

    @PostMapping("/files")
    public ResponseEntity<FileIdResponse> sendFile(@RequestParam("file") MultipartFile file) {
        FileIdResponse response = fileService.uploadFile(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/files/{fileId}/processing")
    public ResponseEntity<Void> processing(@PathVariable String fileId) {
        Integer id = Integer.valueOf(fileId);
        fileProcessingService.ensureExists(id);
        fileProcessingService.process(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/files/statistics")
    public List<FileResponse> getFileStatistic(@RequestParam(required = false) String status) {
        return fileService.getFiles(status);
    }

    @GetMapping("/files/{fileId}/statistics")
    public DetailedFileStatistic getDetailedFileStatistic(@PathVariable String fileId) {
        return fileService.getDetailedStatistic(Integer.valueOf(fileId));
    }
}