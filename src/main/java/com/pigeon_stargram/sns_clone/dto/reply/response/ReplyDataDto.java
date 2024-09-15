package com.pigeon_stargram.sns_clone.dto.reply.response;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.dto.reply.internal.ReplyContentDto;
import lombok.*;

/**
 * 답글 데이터 전송 객체 (DTO)입니다.
 *
 **/
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDataDto {
    private String comment;
    private ReplyLikeDto likes;
}