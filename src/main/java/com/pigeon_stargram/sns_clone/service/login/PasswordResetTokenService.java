package com.pigeon_stargram.sns_clone.service.login;

import com.pigeon_stargram.sns_clone.domain.login.PasswordResetToken;
import com.pigeon_stargram.sns_clone.repository.login.PasswordResetTokenRepository;
import com.pigeon_stargram.sns_clone.util.PasswordResetTokenGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public Boolean validateToken(String token, String email) {
        return tokenRepository.findByTokenAndEmail(token, email)
                .filter(resetToken -> resetToken.getExpiryDate().isAfter(LocalDateTime.now()))
                .isPresent();
    }
}
