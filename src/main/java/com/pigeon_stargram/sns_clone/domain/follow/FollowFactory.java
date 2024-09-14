package com.pigeon_stargram.sns_clone.domain.follow;

import com.pigeon_stargram.sns_clone.domain.user.User;

public class FollowFactory {

    public static Follow createFollow(User sender,
                                      User recipient) {
        return Follow.builder()
                .isNotificationEnabled(false)
                .sender(sender)
                .recipient(recipient)
                .build();
    }
}
