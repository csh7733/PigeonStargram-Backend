package com.pigeon_stargram.sns_clone.dto.comment.request;

import com.pigeon_stargram.sns_clone.domain.post.Posts;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class newCommentDto {
    private String content;
    private List<Long> taggedUserIds;
}