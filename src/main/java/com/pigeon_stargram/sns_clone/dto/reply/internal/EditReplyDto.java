package com.pigeon_stargram.sns_clone.dto.reply.internal;

import lombok.*;

/**
 * EditReplyDto는 답글 수정 요청에 필요한 데이터를 담는 DTO(Data Transfer Object)입니다.
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditReplyDto {

    private Long replyId;
    private String content;
}
