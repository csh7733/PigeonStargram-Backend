package com.pigeon_stargram.sns_clone.dto.reply;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDto {
    private Long id;
    private Long userId;
    private Long commentId;
    private String content;
    private Integer likes;

    public ReplyDto(Reply reply) {
        this.id = reply.getId();
        this.userId = reply.getUser().getId();
        this.commentId = reply.getComment().getId();
        this.content = reply.getContent();
        this.likes = reply.getLikes();
    }
}
