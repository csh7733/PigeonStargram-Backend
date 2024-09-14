package com.pigeon_stargram.sns_clone.dto.Follow;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.*;
import com.pigeon_stargram.sns_clone.dto.Follow.response.ResponseFollowerDto;

public class FollowDtoConverter {

    public static FindFollowingsDto toFindFollowingsDto(Long loginUserId,
                                                        Long userId) {
        return FindFollowingsDto.builder()
                .loginUserId(loginUserId)
                .userId(userId)
                .build();
    }

    public static FindFollowersDto toFindFollowersDto(Long loginUserId,
                                                      Long userId) {
        return FindFollowersDto.builder()
                .loginUserId(loginUserId)
                .userId(userId)
                .build();
    }

    public static GetNotificationEnabledDto toGetNotificationEnabledDto(Long loginUserId,
                                                                        Long targetUserId) {
        return GetNotificationEnabledDto.builder()
                .loginUserId(loginUserId)
                .targetUserId(targetUserId)
                .build();
    }

    public static AddFollowDto toAddFollowDto(Long senderId,
                                              Long recipientId) {
        return AddFollowDto.builder()
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
    }

    public static ToggleNotificationEnabledDto toToggleNotificationEnabledDto(Long loginUserId,
                                                                              Long targetUserId) {
        return ToggleNotificationEnabledDto.builder()
                .loginUserId(loginUserId)
                .targetUserId(targetUserId)
                .build();
    }

    public static DeleteFollowDto toDeleteFollowDto(Long senderId,
                                                    Long recipientId) {
        return DeleteFollowDto.builder()
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
    }

    public static ResponseFollowerDto toResponseFollowerDto(User user,
                                                            Boolean hasUnreadStories) {
        return ResponseFollowerDto.builder()
                .id(user.getId())
                .name(user.getName())
                .location(user.getLocation())
                .avatar(user.getAvatar())
                .follow(1)
                .hasUnreadStories(hasUnreadStories)
                .build();
    }
}
