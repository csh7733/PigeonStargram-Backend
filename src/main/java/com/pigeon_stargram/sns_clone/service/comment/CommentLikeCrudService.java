package com.pigeon_stargram.sns_clone.service.comment;

import java.util.List;

/**
 * 댓글에 대한 좋아요 기능을 제공하는 서비스 인터페이스입니다.
 * <p>
 * 이 인터페이스는 댓글에 대한 좋아요를 토글하고, 좋아요 개수 및 좋아요를 누른 사용자 목록을 조회하는
 * 메서드를 정의합니다. Redis 캐시를 활용하여 성능을 최적화하며, 캐시 미스 시에는 데이터베이스에서
 * 정보를 조회합니다.
 * </p>
 */
public interface CommentLikeCrudService {

    /**
     * 사용자가 특정 댓글에 대해 좋아요를 토글합니다.
     *
     * @param userId 사용자의 ID
     * @param commentId 댓글의 ID
     */
    void toggleLike(Long userId, Long commentId);

    /**
     * 특정 댓글의 좋아요 개수를 반환합니다.
     *
     * @param commentId 댓글의 ID
     * @return 댓글에 대한 좋아요 개수
     */
    Integer countByCommentId(Long commentId);

    /**
     * 특정 댓글에 대해 좋아요를 누른 사용자 ID 목록을 반환합니다.
     *
     * @param commentId 댓글의 ID
     * @return 좋아요를 누른 사용자 ID 목록
     */
    List<Long> getCommentLikeUserIds(Long commentId);
}
