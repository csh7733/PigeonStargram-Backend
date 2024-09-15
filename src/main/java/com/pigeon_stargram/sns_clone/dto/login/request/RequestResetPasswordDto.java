package com.pigeon_stargram.sns_clone.dto.login.request;

import lombok.*;

/**
 * 비밀번호 재설정 요청을 처리하는 DTO 클래스입니다.
 * 재설정 토큰과 새 비밀번호 정보를 담고 있습니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestResetPasswordDto {

    private String token;       // 비밀번호 재설정에 필요한 토큰
    private String newPassword; // 사용자가 설정할 새 비밀번호
}
