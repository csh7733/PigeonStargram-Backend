package com.pigeon_stargram.sns_clone.dto.comment.response;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.dto.reply.response.ReplyDto;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDataDto {
    private String comment;
    private CommentLikeDto likes;
    private List<ReplyDto> replies;

    public CommentDataDto(Comment comment, List<ReplyDto> replies) {
        this.comment = comment.getContent();
        this.likes = new CommentLikeDto(comment);
        this.replies = replies;
    }
}