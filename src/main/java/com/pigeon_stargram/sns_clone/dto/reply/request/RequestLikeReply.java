package com.pigeon_stargram.sns_clone.dto.reply.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestLikeReply {

    @JsonProperty("replayId")
    private Long replyId;
}
