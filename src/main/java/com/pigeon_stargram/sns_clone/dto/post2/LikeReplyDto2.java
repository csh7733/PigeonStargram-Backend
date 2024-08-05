package com.pigeon_stargram.sns_clone.dto.post2;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeReplyDto2 {
    String postId;
    String commentId;
    String replayId;
}
