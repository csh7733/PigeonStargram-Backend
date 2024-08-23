package com.pigeon_stargram.sns_clone.exception.follow;

public class FollowExistException extends RuntimeException{
    public FollowExistException(String message) {
        super(message);
    }

    public FollowExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
