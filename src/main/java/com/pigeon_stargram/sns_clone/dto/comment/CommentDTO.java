package com.pigeon_stargram.sns_clone.dto.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private Long userId;
    private Long postId;
    private String content;
    private Integer likes;

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.userId = comment.getUser().getId();
        this.postId = comment.getPost().getId();
        this.content = comment.getContent();
        this.likes = comment.getLikes();
    }
}
