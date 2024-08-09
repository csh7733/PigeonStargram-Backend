package com.pigeon_stargram.sns_clone.repository.login;

import com.pigeon_stargram.sns_clone.domain.login.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenAndEmail(String token,String email);

    void deleteByToken(String token);
}
