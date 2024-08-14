package com.pigeon_stargram.sns_clone.dto.post.response;

import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.PostsContentDto;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostsDataDto {
    private String content;
    private List<ImageDto> images;
    private PostsLikeDto likes;
    private List<ResponseCommentDto> comments;

    public PostsDataDto(Posts post, List<ResponseCommentDto> comments) {
        this.content = post.getContent();
        this.images = post.getImages().stream()
                .map(ImageDto::new)
                .collect(Collectors.toList());
        this.likes = new PostsLikeDto(post);
        this.comments = comments;
    }

    public PostsDataDto(PostsContentDto contentDto,
                        PostsLikeDto likeDto,
                        List<ResponseCommentDto> commentDtos) {
        this.content = contentDto.getContent();
        this.images = contentDto.getImages();
        this.likes = likeDto;
        this.comments = commentDtos;
    }
}