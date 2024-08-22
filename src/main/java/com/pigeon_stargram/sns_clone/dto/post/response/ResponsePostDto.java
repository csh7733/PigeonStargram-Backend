package com.pigeon_stargram.sns_clone.dto.post.response;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.PostContentDto;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePostDto {
    private Long id;
    private PostProfileDto profile;
    private PostDataDto data;

    public ResponsePostDto(Post post,
                           List<ResponseCommentDto> comments,
                           Integer likeCount) {
        this.id = post.getId();
        this.profile = new PostProfileDto(post.getUser(), post.getModifiedDate());
        this.data = new PostDataDto(post, comments, likeCount);
    }

    public ResponsePostDto(PostContentDto contentDto,
                           PostLikeDto likeDto,
                           List<ResponseCommentDto> commentDtos) {
        this.id = contentDto.getId();
        this.profile = contentDto.getProfile();
        this.data = new PostDataDto(contentDto, likeDto, commentDtos);
    }
}
