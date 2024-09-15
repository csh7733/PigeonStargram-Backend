package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;

import java.util.List;

/**
 * ReplyCrudService 인터페이스는 답글과 관련된 CRUD 작업을 정의한 인터페이스입니다.
 */
public interface ReplyCrudService {

    /**
     * 주어진 ID에 해당하는 답글을 조회합니다.
     *
     * @param replyId 답글의 ID
     * @return 조회된 답글 객체
     */
    Reply findById(Long replyId);

    /**
     * 주어진 댓글 ID에 속한 답글 ID 리스트를 조회합니다.
     *
     * @param commentId 댓글의 ID
     * @return 답글 ID 리스트
     */
    List<Long> findReplyIdByCommentId(Long commentId);

    /**
     * 답글을 저장합니다.
     *
     * @param reply 저장할 답글 객체
     * @return 저장된 답글 객체
     */
    Reply save(Reply reply);

    /**
     * 주어진 답글 ID로 답글을 수정합니다.
     *
     * @param replyId 수정할 답글의 ID
     * @param newContent 새로운 답글 내용
     * @return 수정된 답글 객체
     */
    Reply edit(Long replyId, String newContent);

    /**
     * 주어진 ID에 해당하는 답글을 삭제합니다.
     *
     * @param replyId 삭제할 답글의 ID
     */
    void deleteById(Long replyId);
}
