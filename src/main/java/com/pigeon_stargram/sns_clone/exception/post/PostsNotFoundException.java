package com.pigeon_stargram.sns_clone.exception.post;

public class PostsNotFoundException extends RuntimeException{
    public PostsNotFoundException(String message) {
        super(message);
    }

    public PostsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
