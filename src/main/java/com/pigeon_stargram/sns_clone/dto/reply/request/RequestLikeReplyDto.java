package com.pigeon_stargram.sns_clone.dto.reply.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.transaction.Transactional;
import lombok.*;

/**
 * 답글에 좋아요를 추가하기 위한 데이터 전송 객체 (DTO)입니다.
 *
 * 이 클래스는 사용자가 특정 답글에 좋아요를 추가할 때 필요한 정보를 담고 있으며,
 * 요청 처리 시 필요한 데이터를 포함합니다.
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestLikeReplyDto {
    private Long postId;
    private Long postUserId;
    private Long replyId;
    private String context;
}
