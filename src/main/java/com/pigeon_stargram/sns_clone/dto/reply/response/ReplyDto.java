package com.pigeon_stargram.sns_clone.dto.reply.response;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDto {
    private Long id;
    private ReplyProfileDto profile;
    private ReplyDataDto data;

    public ReplyDto(Reply reply) {
        this.id = reply.getId();
        this.profile = new ReplyProfileDto(reply.getUser(),reply.getModifiedDate());
        this.data = new ReplyDataDto(reply);
    }
}