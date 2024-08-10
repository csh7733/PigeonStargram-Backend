package com.pigeon_stargram.sns_clone.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    CHAT("메시지가 왔어요."),
    FOLLOW("나를 팔로우했어요."),
    FOLLOW_BACK("나를 맞팔했어요."),
    FOLLOWING_POST("새 글이 등록됐어요."),
    MY_POST_COMMENT("댓글이 등록됐어요."),
    MY_POST_LIKE("내 글을 좋아해요."),
    MY_COMMENT_REPLY("댓글이 등록됐어요."),
    MY_COMMENT_LIKE("내 댓글을 좋아해요."),
    MY_REPLY_LIKE("내 댓글을 좋아해요.");

    private final String description;
}
