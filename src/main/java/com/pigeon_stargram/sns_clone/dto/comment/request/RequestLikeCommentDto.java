package com.pigeon_stargram.sns_clone.dto.comment.request;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestLikeCommentDto {
    private Long postId;
    private Long commentId;
    private Long postUserId;
}
