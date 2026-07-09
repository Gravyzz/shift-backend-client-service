package ru.shift.userimporter.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shift.userimporter.core.model.FileProcessingError;

import java.util.List;

public interface FileProcessingErrorRepository extends JpaRepository<FileProcessingError, Integer> {

    List<FileProcessingError> findByFileId(Integer fileId);

    long countByFileId(Integer fileId);
}
