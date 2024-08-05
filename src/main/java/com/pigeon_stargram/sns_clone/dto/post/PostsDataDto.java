package com.pigeon_stargram.sns_clone.dto.post;

import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.dto.comment.CommentDto;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostsDataDto {
    private String content;
    private List<ImageDto> images;
    private PostsLikeDto likes;
    private List<CommentDto> comments;

    public PostsDataDto(Posts post, List<CommentDto> comments) {
        this.content = post.getContent();
        this.images = post.getImages().stream()
                .map(ImageDto::new)
                .collect(Collectors.toList());
        this.likes = new PostsLikeDto(post);
        this.comments = comments;
    }
}