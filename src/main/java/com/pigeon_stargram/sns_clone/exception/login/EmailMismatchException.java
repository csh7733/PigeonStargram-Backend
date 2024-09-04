package com.pigeon_stargram.sns_clone.exception.login;

public class EmailMismatchException extends RuntimeException {
    public EmailMismatchException(String message) {
        super(message);
    }
}
