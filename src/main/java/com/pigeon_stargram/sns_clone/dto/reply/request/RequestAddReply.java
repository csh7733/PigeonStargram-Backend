package com.pigeon_stargram.sns_clone.dto.reply.request;

import com.pigeon_stargram.sns_clone.dto.post2.ReplyDto2;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAddReply {
    private Long commentId;
    private newReplyDto reply;
}
