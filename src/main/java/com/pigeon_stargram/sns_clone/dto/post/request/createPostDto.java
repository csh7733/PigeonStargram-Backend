package com.pigeon_stargram.sns_clone.dto.post.request;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class createPostDto {

    private String content;
}
