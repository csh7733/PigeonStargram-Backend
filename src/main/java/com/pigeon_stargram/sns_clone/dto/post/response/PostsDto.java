package com.pigeon_stargram.sns_clone.dto.post.response;

import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.dto.comment.response.CommentDto;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostsDto {
    private Long id;
    private PostsProfileDto profile;
    private PostsDataDto data;

    public PostsDto(Posts post, List<CommentDto> comments) {
        this.id = post.getId();
        this.profile = new PostsProfileDto(post.getUser(), post.getModifiedDate());
        this.data = new PostsDataDto(post, comments);
    }
}
