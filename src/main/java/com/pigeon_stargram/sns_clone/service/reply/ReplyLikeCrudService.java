package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.domain.reply.ReplyLike;

import java.util.List;
import java.util.Optional;

/**
 * ReplyLikeCrudService는 답글에 대한 좋아요 기능을 정의하는 인터페이스입니다.
 */
public interface ReplyLikeCrudService {

    /**
     * 사용자의 답글에 대한 좋아요 상태를 토글합니다.
     *
     * @param userId  좋아요를 토글할 사용자 ID
     * @param replyId 좋아요를 토글할 답글 ID
     */
    void toggleLike(Long userId, Long replyId);

    /**
     * 주어진 답글 ID에 대해 좋아요 수를 계산합니다.
     *
     * @param replyId 답글 ID
     * @return 답글의 좋아요 수
     */
    Integer countByReplyId(Long replyId);

    /**
     * 주어진 답글 ID에 대해 좋아요를 누른 사용자 ID 리스트를 조회합니다.
     *
     * @param replyId 답글 ID
     * @return 좋아요를 누른 사용자 ID 리스트
     */
    List<Long> getReplyLikeUserIds(Long replyId);

}
