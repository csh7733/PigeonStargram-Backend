package com.pigeon_stargram.sns_clone.exception.file;

public class FileLoadException extends RuntimeException {
    public FileLoadException(String message) {
        super(message);
    }

    public FileLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
