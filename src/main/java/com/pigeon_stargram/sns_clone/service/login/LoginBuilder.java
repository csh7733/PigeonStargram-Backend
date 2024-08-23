package com.pigeon_stargram.sns_clone.service.login;

import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.login.response.UserEmailInfoDto;
import com.pigeon_stargram.sns_clone.dto.login.response.UserInfoDto;
import jakarta.mail.Session;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_OPERATION;

public class LoginBuilder {
    private LoginBuilder() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    public static UserEmailInfoDto buildUserEmailInfoDto(String email) {
        return UserEmailInfoDto.builder()
                .email(email)
                .build();
    }

    public static UserInfoDto buildUserInfoDto(User user) {
        return UserInfoDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .company(user.getCompany())
                .avatar(user.getAvatar())
                .build();
    }

    public static SessionUser buildSessionUser(User user) {
        return SessionUser.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getWorkEmail())
                .picture(user.getAvatar())
                .build();
    }
}
