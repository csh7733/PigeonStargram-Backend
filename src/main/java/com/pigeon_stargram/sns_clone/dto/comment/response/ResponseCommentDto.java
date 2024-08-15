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
public class ResponseCommentDto {
    private Long id;
    private CommentProfileDto profile;
    private CommentDataDto data;

    public ResponseCommentDto(Comment comment,
                              List<ResponseReplyDto> replies,
                              Integer likeCount) {
        this.id = comment.getId();
        this.profile = new CommentProfileDto(comment.getUser(), comment.getModifiedDate());
        this.data = new CommentDataDto(comment, replies, likeCount);
    }

    public ResponseCommentDto(CommentContentDto contentDto,
                              CommentLikeDto likeDto,
                              List<ResponseReplyDto> replyDtos) {
        this.id = contentDto.getId();
        this.profile = contentDto.getProfile();
        this.data = new CommentDataDto(contentDto, likeDto, replyDtos);
    }
}