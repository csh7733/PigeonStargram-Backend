package com.pigeon_stargram.sns_clone.dto.post.response;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeDto {
    private Boolean like;
    private Integer value;

}