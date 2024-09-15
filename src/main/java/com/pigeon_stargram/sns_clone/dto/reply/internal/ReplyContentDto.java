package com.pigeon_stargram.sns_clone.dto.reply.internal;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.dto.comment.response.CommentProfileDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ReplyProfileDto;
import lombok.*;

/**
 * 답글의 콘텐츠 정보를 담기 위한 데이터 전송 객체 (DTO)입니다.
 *
 * 이 클래스는 답글의 ID, 프로필 정보, 그리고 답글 내용 등을 포함하여
 * 클라이언트에게 전달할 데이터를 정의합니다.
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyContentDto {

    private Long id;
    private ReplyProfileDto profile;

    // ReplyDataDto의 필드중 일부
    private String comment;
}
