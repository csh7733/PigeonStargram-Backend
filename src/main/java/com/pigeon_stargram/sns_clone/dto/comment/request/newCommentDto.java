package com.pigeon_stargram.sns_clone.dto.comment.request;

import com.pigeon_stargram.sns_clone.domain.post.Posts;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class newCommentDto {
    private Long userId;
    private String content;
}