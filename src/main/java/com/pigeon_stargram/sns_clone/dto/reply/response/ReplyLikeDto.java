package com.pigeon_stargram.sns_clone.dto.reply.response;

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
}