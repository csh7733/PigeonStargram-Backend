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
public class CommentDto {
    private Long id;
    private CommentProfileDto profile;
    private CommentDataDto data;

    public CommentDto(Comment comment, List<ReplyDto> replies) {
        this.id = comment.getId();
        this.profile = new CommentProfileDto(comment.getUser(), comment.getModifiedDate());
        this.data = new CommentDataDto(comment, replies);
    }

}