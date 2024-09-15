package com.pigeon_stargram.sns_clone.dto.comment.response;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 댓글의 좋아요 정보를 담고 있는 데이터 전송 객체(DTO)입니다.
 * <p>
 * 이 클래스는 사용자가 댓글을 좋아요 했는지 여부와 좋아요의 수를 포함합니다.
 * </p>
 */
@Getter
@Builder
@Setter
@AllArgsConstructor
public class CommentLikeDto {
    private Boolean like;
    private Integer value;
}