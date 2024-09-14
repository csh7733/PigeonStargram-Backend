package com.pigeon_stargram.sns_clone.dto.user;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseUserChatDto;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdateOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdatePasswordDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseLoginUserInfoDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;

/**
 * User 객체와 다양한 DTO 간의 변환을 담당하는 클래스입니다.
 */
public class UserDtoConverter {

    /**
     * User 객체를 ResponseLoginUserDto로 변환합니다.
     *
     * @param user 변환할 User객체
     * @return 변환된 ResponseLoginUserDto
     */
    public static ResponseLoginUserInfoDto toResponseLoginUserInfoDto(User user) {
        return ResponseLoginUserInfoDto.builder()
                .user(user.getId())
                .name(user.getName())
                .company(user.getCompany())
                .avatar(user.getAvatar())
                .isLoggedIn(true)
                .build();
    }

    /**
     * User 객체를 ResponseUserInfoDto로 변환합니다.
     *
     * @param user 변환할 User객체
     * @return 변환된 ResponseUserInfoDto
     */
    public static ResponseUserInfoDto toResponseUserInfoDto(User user) {
        return ResponseUserInfoDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .company(user.getCompany())
                .avatar(user.getAvatar())
                .build();
    }

    /**
     * User 객체를 ResponseUserChatDto로 변환합니다.
     *
     * @param user 변환할 User객체
     * @return 변환된 ResponseUserChatDto
     */
    public static ResponseUserChatDto toResponseUserChatDto(User user) {
        return ResponseUserChatDto.builder()
                .id(user.getId())
                .workEmail(user.getWorkEmail())
                .role(user.getRole().getTitle())
                .name(user.getName())
                .company(user.getCompany())
                .avatar(user.getAvatar())
                .personalPhone(user.getPersonalPhone())
                .workPhone(user.getWorkPhone())
                .personalEmail(user.getPersonalEmail())
                .location(user.getLocation())
                .birthdayText(user.getBirthdayText())
                .onlineStatus(user.getOnlineStatus())
                .unReadChatCount(0)
                .lastMessage("2h ago")
                .status("채팅 기록 없음")
                .state(0)
                .build();
    }

    /**
     * User 객체와 추가 정보를 바탕으로 ResponseUserChatDto를 생성합니다.
     *
     * @param user 변환할 User객체
     * @param unreadChatCount 읽지 않은 채팅 수
     * @param lastMessageDto 마지막 메시지 정보
     * @param state 팔로우 상태
     * @return 변환된 ResponseUserChatDto
     */
    public static ResponseUserChatDto toResponseUserChatDto(User user,
                                                            Integer unreadChatCount,
                                                            LastMessageDto lastMessageDto,
                                                            Integer state) {
        return ResponseUserChatDto.builder()
                .id(user.getId())
                .workEmail(user.getWorkEmail())
                .role(user.getRole().getTitle())
                .name(user.getName())
                .company(user.getCompany())
                .avatar(user.getAvatar())
                .personalPhone(user.getPersonalPhone())
                .workPhone(user.getWorkPhone())
                .personalEmail(user.getPersonalEmail())
                .location(user.getLocation())
                .birthdayText(user.getBirthdayText())
                .onlineStatus(user.getOnlineStatus())
                .unReadChatCount(unreadChatCount)
                .lastMessage(lastMessageDto.getTime())
                .status(lastMessageDto.getLastMessage())
                .state(state)
                .build();
    }

    /**
     * 사용자 ID와 온라인 상태를 바탕으로 UpdateOnlineStatusDto를 생성합니다.
     *
     * @param userId 상태를 업데이트할 사용자 ID
     * @param onlineStatus 온라인 상태
     * @return 변환된 UpdateOnlineStatusDto
     */
    public static UpdateOnlineStatusDto toUpdateOnlineStatusDto(Long userId,
                                                                String onlineStatus) {
        return UpdateOnlineStatusDto.builder()
                .userId(userId)
                .onlineStatus(onlineStatus)
                .build();
    }

    /**
     * 사용자 ID와 온라인 상태를 바탕으로 ResponseOnlineStatusDto를 생성합니다.
     *
     * @param userId 온라인 상태를 변환한 사용자 ID
     * @param onlineStatus 온라인 상태
     * @return 변환된 ResponseOnlineStatusDto
     */
    public static ResponseOnlineStatusDto toResponseOnlineStatusDto(Long userId,
                                                                    String onlineStatus) {
        return ResponseOnlineStatusDto.builder()
                .userId(userId)
                .onlineStatus(onlineStatus)
                .build();
    }

    /**
     * 사용자 ID와 새로운 비밀번호를 바탕으로 UpdatePasswordDto를 생성합니다.
     *
     * @param userId 비밀번호를 업데이트할 사용자 ID
     * @param password 새로운 비밀번호
     * @return 변환된 UpdatePasswordDto
     */
    public static UpdatePasswordDto toUpdatePasswordDto(Long userId,
                                                        String password) {
        return UpdatePasswordDto.builder()
                .userId(userId)
                .password(password)
                .build();
    }
}
