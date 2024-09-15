package com.pigeon_stargram.sns_clone.dto.comment.response;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CommentContentDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ResponseReplyDto;
import lombok.*;

import java.util.List;

/**
 * 댓글에 대한 응답 데이터를 담고 있는 데이터 전송 객체(DTO)입니다.
 * <p>
 * 이 클래스는 댓글의 ID, 댓글 작성자의 프로필 정보, 그리고 댓글 데이터(내용, 좋아요 정보, 답글 목록)를 포함합니다.
 * </p>
 */
@ToString
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCommentDto {
    private Long id;
    private CommentProfileDto profile;
    private CommentDataDto data;
}