package com.pigeon_stargram.sns_clone.dto.reply.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.transaction.Transactional;
import lombok.*;

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
