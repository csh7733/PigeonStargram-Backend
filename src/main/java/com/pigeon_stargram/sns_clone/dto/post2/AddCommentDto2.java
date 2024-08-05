package com.pigeon_stargram.sns_clone.dto.post2;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentDto2 {
    private String postId;
    private CommentDto2 comment;
}
