package com.pigeon_stargram.sns_clone.dto.post.internal;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.dto.post.response.ImageDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostProfileDto;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostContentDto {

    private Long id;
    private PostProfileDto profile;

    // PostsDataDto의 필드중 일부
    private String content;
    private List<ImageDto> images;

    public PostContentDto(Post post) {
        this.id = post.getId();
        this.profile = new PostProfileDto(post.getUser(), post.getModifiedDate());
        this.content = post.getContent();
        this.images = post.getImages().stream()
                .map(ImageDto::new)
                .toList();
    }
}
