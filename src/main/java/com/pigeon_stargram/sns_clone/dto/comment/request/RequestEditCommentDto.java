package com.pigeon_stargram.sns_clone.dto.comment.request;

import lombok.*;

/**
 * 댓글 수정 요청을 위한 데이터 전송 객체(DTO)입니다.
 * <p>
 * 이 클래스는 댓글을 수정하기 위해 필요한 정보를 포함합니다.
 * </p>
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestEditCommentDto {

    private String content;
    private String context;
    private Long postUserId;
}
