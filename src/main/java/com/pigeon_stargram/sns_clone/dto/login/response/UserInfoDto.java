package com.pigeon_stargram.sns_clone.dto.login.response;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

/**
 * 로그인 성공 시 사용자 정보를 담아 반환하는 DTO 클래스입니다.
 * 사용자 ID, 이름, 회사, 아바타 URL을 포함합니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserInfoDto {

    private Long userId;    // 사용자의 고유 ID
    private String name;    // 사용자의 이름
    private String company; // 사용자가 소속된 회사
    private String avatar;  // 사용자의 프로필 사진 URL
}
