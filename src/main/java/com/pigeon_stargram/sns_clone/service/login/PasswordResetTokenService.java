package com.pigeon_stargram.sns_clone.service.login;

import com.pigeon_stargram.sns_clone.domain.login.PasswordResetToken;
import com.pigeon_stargram.sns_clone.exception.login.TokenExpiredException;
import com.pigeon_stargram.sns_clone.exception.login.TokenNotFoundException;
import com.pigeon_stargram.sns_clone.repository.login.PasswordResetTokenRepository;
import com.pigeon_stargram.sns_clone.util.PasswordResetTokenGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository tokenRepository;

    private final PasswordResetTokenGenerator tokenGenerator;

    public PasswordResetToken createToken(String email) {
        String token = tokenGenerator.generateToken();
        LocalDateTime expiryDate = tokenGenerator.calculateExpiryDate();

        PasswordResetToken passwordResetToken = new PasswordResetToken(token, email, expiryDate);
        return tokenRepository.save(passwordResetToken);
    }

    public PasswordResetToken validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException(TOKEN_NOT_FOUND));

        if (!resetToken.getExpiryDate().isAfter(LocalDateTime.now())) {
            throw new TokenExpiredException(TOKEN_EXPIRED);
        }
        return resetToken;
    }

    public String extractEmail(String token) {
        return tokenRepository.findByToken(token)
                .map(PasswordResetToken::getEmail)
                .orElseThrow(() -> new TokenNotFoundException(TOKEN_NOT_FOUND));
    }

}
