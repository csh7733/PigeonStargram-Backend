package com.pigeon_stargram.sns_clone.exception.story;

public class StoryNotFoundException extends RuntimeException {
    public StoryNotFoundException(String message) {
        super(message);
    }

    public StoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
