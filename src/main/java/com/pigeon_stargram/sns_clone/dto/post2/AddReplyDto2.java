package com.pigeon_stargram.sns_clone.dto.post2;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddReplyDto2 {
    private String postId;
    private String commentId;
    private ReplyDto2 reply;
}
