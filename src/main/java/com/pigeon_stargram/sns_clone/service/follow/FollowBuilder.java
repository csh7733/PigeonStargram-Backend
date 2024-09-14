package com.pigeon_stargram.sns_clone.service.follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.AddFollowDto;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.DeleteFollowDto;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.FindFollowersDto;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.FindFollowingsDto;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.GetNotificationEnabledDto;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.ToggleNotificationEnabledDto;
import com.pigeon_stargram.sns_clone.dto.Follow.response.ResponseFollowerDto;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_OPERATION;

public class FollowBuilder {
    private FollowBuilder() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    public static FindFollowingsDto buildFindFollowingsDto(Long loginUserId,
                                                           Long userId) {
        return FindFollowingsDto.builder()
                .loginUserId(loginUserId)
                .userId(userId)
                .build();
    }






    public static Follow buildFollow(User sender,
                                     User recipient) {
        return Follow.builder()
                .sender(sender)
                .recipient(recipient)
                .isNotificationEnabled(false)
                .build();
    }

    public static Follow buildFollow(User sender,
                                     User recipient,
                                     Boolean isNotificationEnabled) {
        return Follow.builder()
                .sender(sender)
                .recipient(recipient)
                .isNotificationEnabled(isNotificationEnabled)
                .build();
    }

    public static ResponseFollowerDto buildResponseFollowerDto(User user, Integer follow) {
        return ResponseFollowerDto.builder()
                .id(user.getId())
                .name(user.getName())
                .avatar(user.getAvatar())
                .location(user.getLocation())
                .follow(follow)
                .build();
    }
}
