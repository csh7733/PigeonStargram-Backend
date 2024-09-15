package com.pigeon_stargram.sns_clone.dto.login.request;

import lombok.*;

/**
 * 로그인 요청 시 사용되는 DTO 클래스입니다.
 * 사용자의 이메일과 비밀번호 정보를 전달하여 인증 요청을 처리합니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestLoginDto {

    private String email;    // 사용자의 이메일 주소
    private String password; // 사용자의 비밀번호
}
