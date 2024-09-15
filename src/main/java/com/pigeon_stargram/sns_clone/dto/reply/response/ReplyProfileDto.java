package com.pigeon_stargram.sns_clone.dto.reply.response;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

import java.time.LocalDateTime;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

/**
 * 답글 작성자의 프로필 정보를 담는 데이터 전송 객체 (DTO)입니다.
 *
 * 이 클래스는 답글을 작성한 사용자의 프로필 사진, 이름, 작성 시간 등의 정보를 포함합니다.
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyProfileDto {
    private Long id;
    private String avatar;
    private String name;
    private String time;
}