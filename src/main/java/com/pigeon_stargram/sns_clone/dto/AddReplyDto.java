package com.pigeon_stargram.sns_clone.dto;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddReplyDto {
    private String postId;
    private String commentId;
    private ReplyDto reply;
}
