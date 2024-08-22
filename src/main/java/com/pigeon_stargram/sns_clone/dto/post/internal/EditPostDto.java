package com.pigeon_stargram.sns_clone.dto.post.internal;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditPostDto {

    private Long postId;
    private String content;
}
