package com.pigeon_stargram.sns_clone.dto.post.response;

import com.pigeon_stargram.sns_clone.domain.post.Posts;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostsLikeDto {
    private Boolean like;
    private Integer value;

    public PostsLikeDto(Posts post) {
        this.like = false;
        this.value = post.getLikes();
    }
}