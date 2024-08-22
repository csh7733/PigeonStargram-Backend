package com.pigeon_stargram.sns_clone.dto.comment.internal;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditCommentDto {
    private Long commentId;
    private String content;
}
