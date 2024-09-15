package com.pigeon_stargram.sns_clone.dto.comment.request;

import lombok.*;

/**
 * 댓글 조회 요청을 위한 데이터 전송 객체(DTO)입니다.
 * <p>
 * 이 클래스는 특정 게시물의 댓글을 가져오기 위해 필요한 정보를 포함합니다.
 * </p>
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestGetCommentDto {

    private Long postId;
    private Long lastCommentId;
}
