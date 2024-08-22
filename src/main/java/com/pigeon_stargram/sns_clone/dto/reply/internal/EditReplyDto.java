package com.pigeon_stargram.sns_clone.dto.reply.internal;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditReplyDto {

    private Long replyId;
    private String content;
}
