package com.pigeon_stargram.sns_clone.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    FOLLOW,
    FOLLOW_BACK,
    FOLLOWING_POST,
    MY_POST_COMMENT,
    MY_POST_LIKE,
    MY_COMMENT_REPLY,
    MY_COMMENT_LIKE,
    MY_REPLY_LIKE,
    POST_TAG,
    COMMENT_TAG,
    REPLY_TAG
}
