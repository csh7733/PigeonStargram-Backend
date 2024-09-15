package com.pigeon_stargram.sns_clone.dto.comment.response;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

import java.time.LocalDateTime;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

/**
 * 댓글 작성자의 프로필 정보를 담고 있는 데이터 전송 객체(DTO)입니다.
 * <p>
 * 이 클래스는 댓글 작성자의 ID, 아바타 이미지, 이름, 그리고 댓글 작성 시간을 포함합니다.
 * </p>
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentProfileDto {
    private Long id;
    private String avatar;
    private String name;
    private String time;
}