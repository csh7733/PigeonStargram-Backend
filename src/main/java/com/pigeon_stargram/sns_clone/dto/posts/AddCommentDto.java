package com.pigeon_stargram.sns_clone.dto.posts;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentDto {
    private String postId;
    private CommentDto comment;
}
