package com.pigeon_stargram.sns_clone.dto.reply.request;

import lombok.*;

/**
 * 새로운 답글을 추가하기 위한 데이터 전송 객체 (DTO)입니다.
 *
 * 이 클래스는 답글이 추가될 게시물 및 댓글 정보와 답글의 내용을 포함합니다.
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAddReplyDto {
    private Long postId;
    private Long commentId;
    private Long postUserId;
    private String context;
    private newReplyDto reply;
}
