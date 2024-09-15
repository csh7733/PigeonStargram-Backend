package com.pigeon_stargram.sns_clone.exception;

import com.pigeon_stargram.sns_clone.exception.comment.CommentNotFoundException;
import com.pigeon_stargram.sns_clone.exception.file.FileLoadException;
import com.pigeon_stargram.sns_clone.exception.file.FileStorageException;
import com.pigeon_stargram.sns_clone.exception.file.FileUploadException;
import com.pigeon_stargram.sns_clone.exception.follow.FollowExistException;
import com.pigeon_stargram.sns_clone.exception.follow.FollowNotFoundException;
import com.pigeon_stargram.sns_clone.exception.login.*;
import com.pigeon_stargram.sns_clone.exception.notification.NotificationNotFoundException;
import com.pigeon_stargram.sns_clone.exception.post.PostNotFoundException;
import com.pigeon_stargram.sns_clone.exception.redis.PatternNotMatchException;
import com.pigeon_stargram.sns_clone.exception.redis.UnsupportedTypeException;
import com.pigeon_stargram.sns_clone.exception.reply.ReplyNotFoundException;
import com.pigeon_stargram.sns_clone.exception.story.StoryNotFoundException;
import com.pigeon_stargram.sns_clone.exception.user.MultipleUsersFoundException;
import com.pigeon_stargram.sns_clone.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // comment 관련 예외 처리
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<String> handleCommentNotFoundException(CommentNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // file 관련 예외 처리
    @ExceptionHandler({
        FileLoadException.class,
        FileStorageException.class,
        FileUploadException.class
    })
    public ResponseEntity<String> handleFileExceptions(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // follow 관련 예외 처리
    @ExceptionHandler(FollowExistException.class)
    public ResponseEntity<String> handleFollowExistException(FollowExistException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FollowNotFoundException.class)
    public ResponseEntity<String> handleFollowNotFoundException(FollowNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // login 관련 예외 처리
    @ExceptionHandler({
        EmailMismatchException.class,
        RegisterFailException.class
    })
    public ResponseEntity<String> handleLoginFailExceptions(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
        EmailNotSentException.class,
        TokenExpiredException.class,
        TokenNotFoundException.class
    })
    public ResponseEntity<String> handleEmailOrTokenExceptions(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // notification 관련 예외 처리
    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<String> handleNotificationNotFoundException(NotificationNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // post 관련 예외 처리
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<String> handlePostNotFoundException(PostNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // redis 관련 예외 처리
    @ExceptionHandler({
        PatternNotMatchException.class,
        UnsupportedTypeException.class
    })
    public ResponseEntity<String> handleRedisExceptions(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // reply 관련 예외 처리
    @ExceptionHandler(ReplyNotFoundException.class)
    public ResponseEntity<String> handleReplyNotFoundException(ReplyNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // story 관련 예외 처리
    @ExceptionHandler(StoryNotFoundException.class)
    public ResponseEntity<String> handleStoryNotFoundException(StoryNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // user 관련 예외 처리
    @ExceptionHandler({
        MultipleUsersFoundException.class,
        UserNotFoundException.class
    })
    public ResponseEntity<String> handleUserExceptions(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 이외 예외 처리
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleGlobalException(Exception ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
