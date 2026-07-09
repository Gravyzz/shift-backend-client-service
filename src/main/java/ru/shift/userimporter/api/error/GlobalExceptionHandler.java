package ru.shift.userimporter.api.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.shift.userimporter.api.dto.Error;
import ru.shift.userimporter.core.exception.DuplicateFileException;
import ru.shift.userimporter.core.exception.UploadedFileNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateFileException.class)
    public ResponseEntity<Error> handleDuplicate(DuplicateFileException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Error(e.getMessage()));
    }

    @ExceptionHandler(UploadedFileNotFoundException.class)
    public ResponseEntity<Error> handleNotFound(UploadedFileNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Error(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleOther(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Error(e.getMessage()));
    }
}
