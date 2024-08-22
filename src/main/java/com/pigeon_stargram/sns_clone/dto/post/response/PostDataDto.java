package com.pigeon_stargram.sns_clone.dto.post.response;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.PostContentDto;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDataDto {
    private String content;
    private List<ImageDto> images;
    private PostLikeDto likes;
    private List<ResponseCommentDto> comments;

    public PostDataDto(Post post,
                       List<ResponseCommentDto> comments,
                       Integer likeCount) {
        this.content = post.getContent();
        this.images = post.getImages().stream()
                .map(ImageDto::new)
                .collect(Collectors.toList());
        this.likes = new PostLikeDto(false, likeCount);
        this.comments = comments;
    }

    public PostDataDto(PostContentDto contentDto,
                       PostLikeDto likeDto,
                       List<ResponseCommentDto> commentDtos) {
        this.content = contentDto.getContent();
        this.images = contentDto.getImages();
        this.likes = likeDto;
        this.comments = commentDtos;
    }
}