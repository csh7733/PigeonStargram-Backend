package com.pigeon_stargram.sns_clone.dto.post.response;

import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.PostsContentDto;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePostsDto {
    private Long id;
    private PostsProfileDto profile;
    private PostsDataDto data;

    public ResponsePostsDto(Posts post,
                            List<ResponseCommentDto> comments,
                            Integer likeCount) {
        this.id = post.getId();
        this.profile = new PostsProfileDto(post.getUser(), post.getModifiedDate());
        this.data = new PostsDataDto(post, comments, likeCount);
    }

    public ResponsePostsDto(PostsContentDto contentDto,
                            PostsLikeDto likeDto,
                            List<ResponseCommentDto> commentDtos) {
        this.id = contentDto.getId();
        this.profile = contentDto.getProfile();
        this.data = new PostsDataDto(contentDto, likeDto, commentDtos);
    }
}
