package com.pigeon_stargram.sns_clone.exception.redis;

public class PatternNotMatchException extends RuntimeException{
    public PatternNotMatchException(String message) {
        super(message);
    }

    public PatternNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
