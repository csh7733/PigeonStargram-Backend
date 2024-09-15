package com.pigeon_stargram.sns_clone.dto.reply.response;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.dto.reply.internal.ReplyContentDto;
import lombok.*;

/**
 * 답글 응답 데이터를 담는 데이터 전송 객체 (DTO)입니다.
 *
 * 이 클래스는 클라이언트에게 반환될 답글의 ID, 작성자 프로필 정보, 답글 데이터 등을 포함합니다.
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseReplyDto {
    private Long id;
    private ReplyProfileDto profile;
    private ReplyDataDto data;
}