package com.pigeon_stargram.sns_clone.dto.comment.request;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestEditCommentDto {
    private String content;
    private Long postUserId;
}
