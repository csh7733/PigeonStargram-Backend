package com.pigeon_stargram.sns_clone.dto.reply;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDataDto {
    private String comment;
    private ReplyLikeDto likes;

    public ReplyDataDto(Reply reply) {
        this.comment = reply.getContent();
        this.likes = new ReplyLikeDto(reply);
    }
}