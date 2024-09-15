package com.pigeon_stargram.sns_clone.dto.login.request;

import lombok.*;

/**
 * 비밀번호 재설정 요청 시 사용되는 DTO 클래스입니다.
 * 사용자의 이메일 주소를 포함하여 비밀번호 재설정 링크 요청에 필요한 데이터를 전달합니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestForgotPasswordDto {

    private String email; // 비밀번호 재설정 링크를 받기 위한 사용자의 이메일 주소
}
