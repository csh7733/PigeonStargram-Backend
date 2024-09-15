package com.pigeon_stargram.sns_clone.dto.comment.response;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CommentContentDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ResponseReplyDto;
import lombok.*;

import java.util.List;

/**
 * 댓글에 대한 상세 정보를 담고 있는 데이터 전송 객체(DTO)입니다.
 * <p>
 * 이 클래스는 댓글의 본문, 좋아요 정보, 그리고 댓글에 대한 답글 목록을 포함합니다.
 * </p>
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDataDto {
    private String comment;
    private CommentLikeDto likes;
    private List<ResponseReplyDto> replies;
}