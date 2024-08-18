package com.pigeon_stargram.sns_clone.exception.reply;

public class ReplyNotFoundException extends RuntimeException{
    public ReplyNotFoundException(String message) {
        super(message);
    }

    public ReplyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
