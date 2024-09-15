package com.pigeon_stargram.sns_clone.dto.reply.request;

import lombok.*;

/**
 * 답글을 삭제하기 위한 데이터 전송 객체 (DTO)입니다.
 *
 * 이 클래스는 삭제할 답글에 대한 정보를 담고 있으며, 해당 답글을 식별하는 데 필요한 데이터를 포함합니다.
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDeleteReplyDto {
    private Long postUserId;
    private String context;
}
