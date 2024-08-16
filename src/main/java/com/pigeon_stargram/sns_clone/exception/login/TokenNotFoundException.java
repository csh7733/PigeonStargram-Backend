package com.pigeon_stargram.sns_clone.exception.login;

public class TokenNotFoundException extends RuntimeException{

    public TokenNotFoundException(String message) {
        super(message);
    }

    public TokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
