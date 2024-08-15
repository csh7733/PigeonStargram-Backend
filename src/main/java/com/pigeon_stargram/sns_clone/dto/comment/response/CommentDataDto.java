package com.pigeon_stargram.sns_clone.dto.comment.response;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CommentContentDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ResponseReplyDto;
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
    private List<ResponseReplyDto> replies;

    public CommentDataDto(Comment comment, List<ResponseReplyDto> replies) {
        this.comment = comment.getContent();
        this.likes = new CommentLikeDto(comment);
        this.replies = replies;
    }

    public CommentDataDto(CommentContentDto contentDto,
                          CommentLikeDto likeDto,
                          List<ResponseReplyDto> replyDtos) {
        this.comment = contentDto.getComment();
        this.likes = likeDto;
        this.replies = replyDtos;
    }
}