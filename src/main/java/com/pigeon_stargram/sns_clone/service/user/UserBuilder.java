package com.pigeon_stargram.sns_clone.service.user;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseUserChatDto;
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
                .user(user.getId())
                .name(user.getName())
                .company(user.getCompany())
                .avatar(user.getAvatar())
                .isLoggedIn(true)
                .build();
    }

    public static ResponseUserInfoDto buildResponseUserInfoDto(User user) {
        return ResponseUserInfoDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .avatar(user.getAvatar())
                .company(user.getCompany())
                .build();
    }

    public static ResponseOnlineStatusDto buildResponseOnlineStatusDto(User user) {
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

    public static ResponseUserChatDto buildResponseUserChatDto(User user) {
        return ResponseUserChatDto.builder()
                .id(user.getId())
                .name(user.getName())
                .company(user.getCompany())
                .workEmail(user.getWorkEmail())
                .personalPhone(user.getPersonalPhone())
                .location(user.getLocation())
                .avatar(user.getAvatar())
                .status("채팅 기록 없음")
                //temp
                .lastMessage("2h ago")
                .birthdayText(user.getBirthdayText())
                //temp
                .unReadChatCount(0)
                .onlineStatus(user.getOnlineStatus())
                .build();
    }

    public static ResponseUserChatDto buildResponseUserChatDto(User user,
                                                               Integer unreadChatCount,
                                                               LastMessageDto lastMessageDto,
                                                               Integer state) {
        return ResponseUserChatDto.builder()
                .id(user.getId())
                .name(user.getName())
                .company(user.getCompany())
                .workEmail(user.getWorkEmail())
                .personalEmail(user.getPersonalEmail())
                .workPhone(user.getWorkPhone())
                .personalPhone(user.getPersonalPhone())
                .location(user.getLocation())
                .avatar(user.getAvatar())
                .status(lastMessageDto.getLastMessage())
                .lastMessage(lastMessageDto.getTime())
                .birthdayText(user.getBirthdayText())
                //temp
                .unReadChatCount(unreadChatCount)
                .onlineStatus(user.getOnlineStatus())
                .state(state)
                .build();
    }

}
