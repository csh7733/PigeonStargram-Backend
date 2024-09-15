package com.pigeon_stargram.sns_clone.dto.comment.request;

import lombok.*;

/**
 * 댓글에 좋아요를 요청하기 위한 데이터 전송 객체(DTO)입니다.
 * <p>
 * 이 클래스는 특정 댓글에 좋아요를 추가하거나 제거하기 위해 필요한 정보를 포함합니다.
 * </p>
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestLikeCommentDto {
    private Long postId;
    private Long commentId;
    private Long postUserId;
    private String context;
}
