package com.pigeon_stargram.sns_clone.dto.reply.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeReplyDto {

    @JsonProperty("replayId")
    private Long replyId;
}
