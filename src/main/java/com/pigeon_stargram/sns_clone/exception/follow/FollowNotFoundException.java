package com.pigeon_stargram.sns_clone.exception.follow;

public class FollowNotFoundException extends RuntimeException{

    public FollowNotFoundException(String message) {
        super(message);
    }

    public FollowNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
