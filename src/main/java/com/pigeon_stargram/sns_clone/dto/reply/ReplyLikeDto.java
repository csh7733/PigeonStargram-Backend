package com.pigeon_stargram.sns_clone.dto.reply;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyLikeDto {
    private Boolean like;
    private Integer value;

    public ReplyLikeDto(Reply reply) {
        this.like = false;
        this.value = reply.getLikes();
    }
}