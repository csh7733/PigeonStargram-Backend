package com.pigeon_stargram.sns_clone.service.user;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdateOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdatePasswordDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseLoginUserDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_OPERATION;

public class UserBuilder {

    private UserBuilder() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    public static ResponseLoginUserDto buildLoginUserDto(User user) {
        return ResponseLoginUserDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .company(user.getCompany())
                .avatar(user.getAvatar())
                .isLoggedIn(true)
                .build();
    }

    public static ResponseUserInfoDto buildResponseUserInfo(User user) {
        return ResponseUserInfoDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .avatar(user.getAvatar())
                .company(user.getCompany())
                .build();
    }

    public static ResponseOnlineStatusDto buildResponseOnlineStatus(User user) {
        return ResponseOnlineStatusDto.builder()
                .userId(user.getId())
                .onlineStatus(user.getOnlineStatus())
                .build();
    }

    public static UpdateOnlineStatusDto buildUpdateOnlineStatusDto(Long userId,
                                                                   String onlineStatus) {
        return UpdateOnlineStatusDto.builder()
                .userId(userId)
                .onlineStatus(onlineStatus)
                .build();
    }

    public static UpdatePasswordDto buildUpdatePasswordDto(Long userId,
                                                           String password) {
        return UpdatePasswordDto.builder()
                .userId(userId)
                .password(password)
                .build();
    }

}
