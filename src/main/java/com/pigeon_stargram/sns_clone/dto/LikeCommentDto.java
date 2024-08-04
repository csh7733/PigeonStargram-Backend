package com.pigeon_stargram.sns_clone.dto;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeCommentDto {
    String postId;
    String commentId;
}
