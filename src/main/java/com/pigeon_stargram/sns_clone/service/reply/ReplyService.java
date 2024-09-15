package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.dto.reply.internal.CreateReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.EditReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.LikeReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.ReplyContentDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ReplyLikeDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ResponseReplyDto;

import java.util.List;

/**
 * ReplyService는 답글과 관련된 주요 비즈니스 로직을 처리하는 메서드들을 정의한 인터페이스입니다.
 */
public interface ReplyService {

    /**
     * 주어진 답글 ID에 해당하는 답글을 조회합니다.
     *
     * @param replyId 답글의 ID
     * @return 조회된 답글 객체
     */
    Reply findById(Long replyId);

    /**
     * 주어진 댓글 ID에 속한 모든 답글을 DTO 리스트로 반환합니다.
     *
     * @param commentId 댓글의 ID
     * @return 답글 DTO 리스트
     */
    List<ResponseReplyDto> getReplyDtosByCommentId(Long commentId);

    /**
     * 답글 ID로 답글 내용 및 좋아요 정보를 조합하여 응답 DTO를 반환합니다.
     *
     * @param replyId 답글의 ID
     * @return 응답 DTO
     */
    ResponseReplyDto getCombinedReply(Long replyId);

    /**
     * 주어진 답글의 내용을 조회하여 반환합니다.
     *
     * @param replyId 답글의 ID
     * @return 답글 내용 DTO
     */
    ReplyContentDto getReplyContent(Long replyId);

    /**
     * 주어진 답글의 좋아요 정보를 조회하여 반환합니다.
     *
     * @param replyId 답글의 ID
     * @return 답글 좋아요 DTO
     */
    ReplyLikeDto getReplyLike(Long replyId);

    /**
     * 새로운 답글을 생성합니다.
     *
     * @param dto 답글 생성 요청 DTO
     * @return 생성된 답글의 응답 DTO
     */
    ResponseReplyDto createReply(CreateReplyDto dto);

    /**
     * 주어진 답글을 수정합니다.
     *
     * @param dto 답글 수정 요청 DTO
     */
    void editReply(EditReplyDto dto);

    /**
     * 주어진 답글에 좋아요를 추가하거나 삭제합니다.
     *
     * @param dto 답글 좋아요 요청 DTO
     * @return 좋아요가 추가되면 true, 삭제되면 false 반환
     */
    Boolean likeReply(LikeReplyDto dto);

    /**
     * 주어진 댓글 ID에 속한 모든 답글을 삭제합니다.
     *
     * @param commentId 댓글의 ID
     */
    void deleteAllReplyByCommentId(Long commentId);
}
