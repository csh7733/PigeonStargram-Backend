package com.pigeon_stargram.sns_clone.dto.post2;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditCommentDto2 {
    private String key;
    private CommentDto2 id;
}
