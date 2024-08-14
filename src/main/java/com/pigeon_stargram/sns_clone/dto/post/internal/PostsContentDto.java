package com.pigeon_stargram.sns_clone.dto.post.internal;

import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.dto.post.response.ImageDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostsProfileDto;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostsContentDto {

    private Long id;
    private PostsProfileDto profile;

    // PostsDataDto의 필드중 일부
    private String content;
    private List<ImageDto> images;

    public PostsContentDto(Posts post) {
        this.id = post.getId();
        this.profile = new PostsProfileDto(post.getUser(), post.getModifiedDate());
        this.content = post.getContent();
        this.images = post.getImages().stream()
                .map(ImageDto::new)
                .toList();
    }
}
