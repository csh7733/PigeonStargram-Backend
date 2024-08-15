package com.pigeon_stargram.sns_clone.dto.reply.response;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.dto.reply.internal.ReplyContentDto;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseReplyDto {
    private Long id;
    private ReplyProfileDto profile;
    private ReplyDataDto data;

    public ResponseReplyDto(Reply reply, Integer likeCount) {
        this.id = reply.getId();
        this.profile = new ReplyProfileDto(reply.getUser(),reply.getModifiedDate());
        this.data = new ReplyDataDto(reply, likeCount);
    }

    public ResponseReplyDto(ReplyContentDto contentDto,
                            ReplyLikeDto likeDto) {
        this.id = contentDto.getId();
        this.profile = contentDto.getProfile();
        this.data = new ReplyDataDto(contentDto, likeDto);
    }
}