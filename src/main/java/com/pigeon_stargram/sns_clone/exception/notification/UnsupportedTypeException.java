package com.pigeon_stargram.sns_clone.exception.notification;

public class UnsupportedTypeException extends RuntimeException{

    public UnsupportedTypeException(String message) {
        super(message);
    }

    public UnsupportedTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
