package ru.shift.userimporter.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    @Value("${app.storage-dir}")
    private String storageDir;

    public String store(byte[] bytes, String filename) {
        try {
            Path dir = Paths.get(storageDir);
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            Files.write(target, bytes);
            return target.toString();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл", e);
        }
    }
}
