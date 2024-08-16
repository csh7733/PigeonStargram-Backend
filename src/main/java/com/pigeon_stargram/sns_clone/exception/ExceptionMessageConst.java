package com.pigeon_stargram.sns_clone.exception;

public class ExceptionMessageConst {

    public static final String USER_NOT_FOUND_ID
            = "ID가 일치하는 유저가 없습니다.";
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

    public static final String TOKEN_NOT_FOUND
            = "존재하지 않는 토큰입니다.";
    public static final String TOKEN_EXPIRED
            = "만료된 토큰입니다.";
}
