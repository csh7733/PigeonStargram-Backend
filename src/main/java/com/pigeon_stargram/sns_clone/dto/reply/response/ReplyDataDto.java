package com.pigeon_stargram.sns_clone.dto.reply.response;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.dto.reply.internal.ReplyContentDto;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDataDto {
    private String comment;
    private ReplyLikeDto likes;

    public ReplyDataDto(Reply reply, Integer likeCount) {
        this.comment = reply.getContent();
        this.likes = new ReplyLikeDto(false, likeCount);
    }

    public ReplyDataDto(ReplyContentDto contentDto,
                        ReplyLikeDto likeDto) {
        this.comment = contentDto.getComment();
        this.likes = likeDto;
    }
}