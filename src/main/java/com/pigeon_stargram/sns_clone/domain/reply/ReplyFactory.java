package com.pigeon_stargram.sns_clone.domain.reply;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.reply.internal.CreateReplyDto;

/**
 * 답글 관련 객체를 생성하는 팩토리 클래스입니다.
 *
 * 이 클래스는 주어진 데이터 전송 객체(DTO)와 도메인 모델을 기반으로
 * `Reply` 및 `ReplyLike` 객체를 생성하는 메서드를 제공합니다.
 */
public class ReplyFactory {

    /**
     * CreateReplyDto를 사용하여 Reply 객체를 생성합니다.
     *
     * @param dto 답글 생성에 필요한 데이터 전송 객체
     * @param user 답글 작성자
     * @param comment 댓글 정보
     * @return 생성된 Reply 객체
     */
    public static Reply createReply(CreateReplyDto dto,
                                    User user,
                                    Comment comment) {
        return Reply.builder()
                .user(user)
                .comment(comment)
                .content(dto.getContent())
                .build();
    }

    /**
     * User와 Reply를 사용하여 ReplyLike 객체를 생성합니다.
     *
     * @param loginUser 로그인 사용자
     * @param reply 답글 정보
     * @return 생성된 ReplyLike 객체
     */
    public static ReplyLike createReplyLike(User loginUser,
                                            Reply reply) {
        return ReplyLike.builder()
                .user(loginUser)
                .reply(reply)
                .build();
    }
}
