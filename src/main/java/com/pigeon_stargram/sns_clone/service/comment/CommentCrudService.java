package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;

import java.util.List;

/**
 * 댓글 CRUD 서비스를 위한 인터페이스입니다.
 * <p>
 * 이 인터페이스는 댓글에 대한 CRUD 작업을 정의합니다. 실제 구현체에서는
 * Redis 캐시와 데이터베이스를 연동하여 댓글 정보를 효율적으로 관리합니다.
 * </p>
 */
public interface CommentCrudService {

    /**
     * 댓글 ID를 기반으로 댓글을 조회합니다.
     *
     * @param commentId 댓글 ID
     * @return 댓글 객체
     */
    Comment findById(Long commentId);

    /**
     * 게시물 ID를 기준으로 댓글 ID 목록을 조회합니다.
     *
     * @param postId 게시물 ID
     * @return 댓글 ID 리스트
     */
    List<Long> findCommentIdByPostId(Long postId);

    /**
     * 게시물 ID와 특정 댓글 ID를 기준으로 댓글 ID 목록을 조회합니다.
     *
     * @param postId    게시물 ID
     * @param commentId 댓글 ID
     * @return 댓글 ID 리스트
     */
    List<Long> findCommentIdByPostIdAndCommentId(Long postId, Long commentId);

    /**
     * 댓글을 저장하고 관련 캐시를 업데이트합니다.
     *
     * @param comment 저장할 댓글 객체
     * @return 저장된 댓글 객체
     */
    Comment save(Comment comment);

    /**
     * 댓글의 내용을 수정합니다. 캐시를 업데이트합니다.
     *
     * @param commentId 수정할 댓글 ID
     * @param newContent 새 댓글 내용
     * @return 수정된 댓글 객체
     */
    Comment edit(Long commentId, String newContent);

    /**
     * 댓글을 삭제하고 관련 캐시를 제거합니다.
     *
     * @param commentId 삭제할 댓글 ID
     */
    void deleteById(Long commentId);

    /**
     * 댓글이 더 있는지 여부를 확인합니다.
     *
     * @param postId        게시물 ID
     * @param lastCommentId 마지막 댓글 ID
     * @return 댓글이 더 있는지 여부
     */
    Boolean getIsMoreComments(Long postId, Long lastCommentId);
}
