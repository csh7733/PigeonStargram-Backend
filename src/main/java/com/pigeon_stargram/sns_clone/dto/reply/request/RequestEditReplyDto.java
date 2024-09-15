package com.pigeon_stargram.sns_clone.dto.reply.request;

import lombok.*;

/**
 * 답글을 수정하기 위한 데이터 전송 객체 (DTO)입니다.
 *
 * 이 클래스는 수정할 답글의 정보를 담고 있으며, 수정 작업을 수행하는 데 필요한 데이터를 포함합니다.
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestEditReplyDto {
    private String content;
    private String context;
    private Long postUserId;
}
