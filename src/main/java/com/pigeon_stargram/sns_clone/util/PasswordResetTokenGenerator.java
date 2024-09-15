package com.pigeon_stargram.sns_clone.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 비밀번호 재설정 토큰을 생성하고 만료 시간을 계산하는 유틸리티 클래스입니다.
 */
@Component
public class PasswordResetTokenGenerator {
    private static final long TOKEN_VALIDITY_DURATION = 60;

    /**
     * 고유한 비밀번호 재설정 토큰을 생성합니다.
     *
     * @return 생성된 UUID 형식의 토큰 문자열
     */
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * 토큰의 만료 시간을 계산합니다.
     *
     * @return 현재 시간으로부터 TOKEN_VALIDITY_DURATION 분 후의 만료 시간
     */
    public LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusMinutes(TOKEN_VALIDITY_DURATION);
    }
}
