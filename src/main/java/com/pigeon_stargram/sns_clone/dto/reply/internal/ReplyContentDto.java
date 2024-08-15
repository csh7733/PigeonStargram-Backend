package com.pigeon_stargram.sns_clone.dto.reply.internal;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.dto.comment.response.CommentProfileDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ReplyProfileDto;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyContentDto {

    private Long id;
    private ReplyProfileDto profile;

    // ReplyDataDto의 필드중 일부
    private String comment;

    public ReplyContentDto(Reply reply) {
        this.id = reply.getId();
        this.profile = new ReplyProfileDto(reply.getUser(), reply.getModifiedDate());
        this.comment = reply.getContent();
    }
}
