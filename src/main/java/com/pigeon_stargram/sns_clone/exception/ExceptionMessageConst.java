package com.pigeon_stargram.sns_clone.exception;

public class ExceptionMessageConst {

    public static final String UNSUPPORTED_OPERATION
            = "지원하지 않는 생성자입니다.";

    public static final String USER_NOT_FOUND_ID
            = "ID가 일치하는 유저가 없습니다.";
    public static final String USER_NOT_FOUND_NAME
            = "이름과 일치하는 유저가 없습니다.";
    public static final String USER_NOT_FOUND_EMAIL
            = "이메일이 일치하는 유저가 없습니다.";
    public static final String USER_NOT_FOUND_EMAIL_PASSWORD
            = "이메일과 비밀번호가 일치하는 유저가 없습니다.";

    public static final String MULTIPLE_USERS_FOUND_EMAIL
            = "이메일이 일치하는 유저가 없습니다.";
    public static final String MULTIPLE_USERS_FOUND_EMAIL_PASSWORD
            = "이메일과 비밀번호가 일치하는 유저가 없습니다.";

    public static final String REGISTER_FAIL_EMAIL
            = "이미 사용중인 이메일입니다.";

    public static final String EMAIL_NOT_SENT
            = "이메일을 보내지 못했습니다";

    public static final String EMAIL_MISMATCH
            = "제공된 이메일이 요청에 포함된 이메일과 일치하지 않습니다.";

    public static final String TOKEN_NOT_FOUND
            = "존재하지 않는 토큰입니다.";
    public static final String TOKEN_EXPIRED
            = "만료된 토큰입니다.";

    public static final String POST_NOT_FOUND_ID
            = "ID와 일치하는 게시물이 없습니다.";

    public static final String COMMENT_NOT_FOUND_ID
            = "ID와 일치하는 댓글이 없습니다.";

    public static final String REPLY_NOT_FOUND_ID
            = "ID와 일치하는 답글이 없습니다.";
    
    public static final String NOTIFICATION_NOT_FOUND_ID
            = "ID와 일치하는 알림이 없습니다.";

    public static final String FOLLOW_EXIST
            = "이미 팔로우중입니다.";
    public static final String FOLLOW_NOT_FOUND
            = "팔로우 정보가 없습니다.";
    public static final String STORY_NOT_FOUND_ID
            = "ID와 일치하는 스토리가 없습니다";

    public static final String FILE_UPLOAD_FAIL
            = "파일 업로드가 실패했습니다";

    public static final String FILE_NOT_FOUND_OR_UNREADABLE
            = "S3에서 파일을 찾을 수 없거나 읽을 수 없습니다: ";
    public static final String MALFORMED_URL
            = "S3에서 파일의 URL 형식이 잘못되었습니다: ";
}
