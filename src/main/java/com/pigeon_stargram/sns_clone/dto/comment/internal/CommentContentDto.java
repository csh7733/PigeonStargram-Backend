package com.pigeon_stargram.sns_clone.dto.comment.internal;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.dto.comment.response.CommentProfileDto;
import lombok.*;

/**
 * 댓글의 내용과 관련된 정보를 담는 DTO입니다.
 * <p>
 * 이 클래스는 댓글 ID, 프로필 정보, 댓글 본문 등을 포함합니다.
 * </p>
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentContentDto {

    private Long id;
    private CommentProfileDto profile;  // 댓글 작성자의 프로필 정보를 담고 있는 DTO입니다.

    // CommentDataDto의 필드중 일부
    private String comment; // 댓글 본문입니다.
}
