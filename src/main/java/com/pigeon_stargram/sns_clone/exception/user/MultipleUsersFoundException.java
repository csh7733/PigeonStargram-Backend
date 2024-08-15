package com.pigeon_stargram.sns_clone.exception.user;

public class MultipleUsersFoundException extends RuntimeException {

    public MultipleUsersFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultipleUsersFoundException(String message) {
        super(message);
    }
}
