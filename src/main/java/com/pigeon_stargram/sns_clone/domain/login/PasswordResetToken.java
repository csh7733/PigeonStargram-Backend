package com.pigeon_stargram.sns_clone.domain.login;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 비밀번호 재설정 토큰을 저장하는 엔티티 클래스입니다.
 * 비밀번호 재설정을 위한 토큰과 이메일, 만료 날짜를 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token; // 비밀번호 재설정을 위한 토큰 문자열
    private String email; // 토큰이 발급된 이메일 주소
    private LocalDateTime expiryDate; // 토큰의 만료 날짜와 시간

    @Builder
    public PasswordResetToken(String token, String email, LocalDateTime expiryDate) {
        this.token = token;
        this.email = email;
        this.expiryDate = expiryDate;
    }
}
