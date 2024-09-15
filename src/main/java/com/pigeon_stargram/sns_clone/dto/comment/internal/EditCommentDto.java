package com.pigeon_stargram.sns_clone.dto.comment.internal;

import lombok.*;

/**
 * 댓글 수정을 위한 데이터 전송 객체(DTO)입니다.
 * <p>
 * 이 클래스는 댓글 수정에 필요한 댓글 ID와 새 콘텐츠를 포함합니다.
 * </p>
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditCommentDto {

    private Long commentId;
    private String content;
}
