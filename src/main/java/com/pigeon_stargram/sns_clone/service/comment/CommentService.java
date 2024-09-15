package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CommentContentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.EditCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.LikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestGetCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.CommentLikeDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseGetCommentDto;

import java.util.List;
/**
 * 댓글 관련 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 */
public interface CommentService {

    /**
     * 댓글을 ID로 조회합니다.
     *
     * @param commentId 조회할 댓글의 ID
     * @return 조회된 댓글
     */
    Comment findById(Long commentId);

    /**
     * 포스트 ID와 마지막 댓글 ID를 기준으로 댓글을 조회합니다.
     *
     * @param dto 조회에 필요한 데이터가 담긴 DTO
     * @return 조회된 댓글과 추가 댓글 여부를 포함하는 DTO
     */
    ResponseGetCommentDto getPartialComment(RequestGetCommentDto dto);

    /**
     * 포스트 ID와 마지막 댓글 ID를 기준으로 댓글 목록을 조회합니다.
     *
     * @param postId 포스트 ID
     * @param commentId 마지막 댓글 ID
     * @return 조회된 댓글 목록
     */
    List<ResponseCommentDto> getCommentResponseByPostIdAndLastCommentId(Long postId, Long commentId);

    /**
     * 댓글 ID로 댓글과 관련된 정보를 결합하여 조회합니다.
     *
     * @param commentId 조회할 댓글의 ID
     * @return 결합된 댓글 정보 DTO
     */
    ResponseCommentDto getCombinedComment(Long commentId);

    /**
     * 댓글 ID로 댓글 내용을 조회합니다.
     *
     * @param commentId 조회할 댓글의 ID
     * @return 댓글 내용 DTO
     */
    CommentContentDto getCommentContent(Long commentId);

    /**
     * 새로운 댓글을 생성합니다.
     *
     * @param dto 생성할 댓글에 대한 데이터가 담긴 DTO
     * @return 생성된 댓글 정보 DTO
     */
    ResponseCommentDto createComment(CreateCommentDto dto);

    /**
     * 댓글을 수정합니다.
     *
     * @param dto 수정할 댓글에 대한 데이터가 담긴 DTO
     */
    void editComment(EditCommentDto dto);

    /**
     * 포스트 ID를 기준으로 댓글과 답글을 모두 삭제합니다.
     *
     * @param postId 삭제할 포스트의 ID
     */
    void deleteAllCommentsAndReplyByPostId(Long postId);

    /**
     * 댓글 ID를 기준으로 댓글을 삭제합니다.
     *
     * @param commentId 삭제할 댓글의 ID
     */
    void deleteComment(Long commentId);

    /**
     * 댓글 ID를 기준으로 댓글의 좋아요 수를 조회합니다.
     *
     * @param commentId 조회할 댓글의 ID
     * @return 댓글 좋아요 정보 DTO
     */
    CommentLikeDto getCommentLike(Long commentId);

    /**
     * 댓글에 좋아요를 추가하거나 제거합니다.
     *
     * @param dto 좋아요를 처리할 댓글에 대한 데이터가 담긴 DTO
     * @return 좋아요가 추가되었는지 여부
     */
    Boolean likeComment(LikeCommentDto dto);
}
