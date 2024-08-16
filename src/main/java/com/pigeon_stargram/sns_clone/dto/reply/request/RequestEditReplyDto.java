package com.pigeon_stargram.sns_clone.dto.reply.request;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestEditReplyDto {
    private String content;
    private Long postUserId;
}
