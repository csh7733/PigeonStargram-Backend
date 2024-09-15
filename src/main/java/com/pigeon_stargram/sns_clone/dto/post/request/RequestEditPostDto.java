package com.pigeon_stargram.sns_clone.dto.post.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestEditPostDto {

    private String content;
}
