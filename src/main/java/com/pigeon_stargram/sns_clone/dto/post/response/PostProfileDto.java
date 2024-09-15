package com.pigeon_stargram.sns_clone.dto.post.response;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

import java.time.LocalDateTime;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

/**
 * PostProfileDto는 게시물 작성자의 프로필 정보를 표현하는 DTO(Data Transfer Object)입니다.
 * 이 DTO는 게시물과 함께 작성자의 기본 정보를 클라이언트에 전달하기 위해 사용됩니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostProfileDto {

    private Long id;        // 작성자의 고유 식별자
    private String avatar;  // 작성자의 프로필 이미지 URL
    private String name;    // 작성자의 이름 또는 닉네임
    private String time;    // 게시물이 작성된 시간

}