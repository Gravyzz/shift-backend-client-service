package ru.shift.userimporter.core.exception;

public class UploadedFileNotFoundException extends RuntimeException {

    public UploadedFileNotFoundException(String message) {
        super(message);
    }
}
