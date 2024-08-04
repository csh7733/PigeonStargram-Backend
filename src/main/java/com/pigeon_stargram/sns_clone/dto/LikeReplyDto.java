package com.pigeon_stargram.sns_clone.dto;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeReplyDto {
    String postId;
    String commentId;
    String replayId;
}
