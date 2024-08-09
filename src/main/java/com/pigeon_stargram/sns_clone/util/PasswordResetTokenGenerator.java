package com.pigeon_stargram.sns_clone.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PasswordResetTokenGenerator {
    private static final long TOKEN_VALIDITY_DURATION = 60;
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusMinutes(TOKEN_VALIDITY_DURATION);
    }
}
