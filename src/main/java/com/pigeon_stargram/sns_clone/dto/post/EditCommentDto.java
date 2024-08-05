package com.pigeon_stargram.sns_clone.dto.post;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditCommentDto {
    private String key;
    private CommentDto id;
}
