package com.pigeon_stargram.sns_clone.dto.comment.internal;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.dto.comment.response.CommentProfileDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ImageDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostsProfileDto;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentContentDto {

    private Long id;
    private CommentProfileDto profile;

    // CommentDataDto의 필드중 일부
    private String comment;

    public CommentContentDto(Comment comment) {
        this.id = comment.getId();
        this.profile = new CommentProfileDto(comment.getUser(), comment.getModifiedDate());
        this.comment = comment.getContent();
    }
}
