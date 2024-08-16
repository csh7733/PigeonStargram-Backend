package com.pigeon_stargram.sns_clone.exception.login;

public class RegisterFailException extends RuntimeException{

    public RegisterFailException(String message) {
        super(message);
    }

    public RegisterFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
