package com.pigeon_stargram.sns_clone.dto.reply.response;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import lombok.*;

/**
 * 답글의 좋아요 정보를 담는 데이터 전송 객체 (DTO)입니다.
 *
 * 이 클래스는 사용자가 답글을 좋아요 했는지 여부와 좋아요 수를 포함합니다.
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyLikeDto {
    private Boolean like;
    private Integer value;
}