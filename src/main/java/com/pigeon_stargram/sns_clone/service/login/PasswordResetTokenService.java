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

/**
 * 비밀번호 재설정 토큰을 생성하고 검증하는 서비스 클래스입니다.
 * <p>
 * 사용자가 비밀번호를 재설정할 때 사용하는 토큰을 생성하고,
 * 해당 토큰이 유효한지 확인하는 역할을 합니다.
 * </p>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository tokenRepository;

    private final PasswordResetTokenGenerator tokenGenerator;

    /**
     * 주어진 이메일을 기반으로 비밀번호 재설정 토큰을 생성합니다.
     *
     * @param email 비밀번호 재설정을 요청한 사용자의 이메일 주소
     * @return 생성된 비밀번호 재설정 토큰 객체
     */
    public PasswordResetToken createToken(String email) {
        String token = tokenGenerator.generateToken();
        LocalDateTime expiryDate = tokenGenerator.calculateExpiryDate();

        PasswordResetToken passwordResetToken = new PasswordResetToken(token, email, expiryDate);
        return tokenRepository.save(passwordResetToken);
    }

    /**
     * 주어진 토큰의 유효성을 검증합니다.
     * <p>
     * 토큰이 유효하지 않거나 만료되었을 경우 예외를 던집니다.
     * </p>
     *
     * @param token 검증할 비밀번호 재설정 토큰
     * @return 유효한 비밀번호 재설정 토큰 객체
     * @throws TokenNotFoundException 토큰이 존재하지 않는 경우
     * @throws TokenExpiredException 토큰이 만료된 경우
     */
    public PasswordResetToken validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException(TOKEN_NOT_FOUND));

        if (!resetToken.getExpiryDate().isAfter(LocalDateTime.now())) {
            throw new TokenExpiredException(TOKEN_EXPIRED);
        }
        return resetToken;
    }

    /**
     * 주어진 토큰으로부터 이메일을 추출합니다.
     *
     * @param token 비밀번호 재설정 토큰
     * @return 해당 토큰에 연결된 이메일 주소
     * @throws TokenNotFoundException 토큰이 존재하지 않는 경우
     */
    public String extractEmail(String token) {
        return tokenRepository.findByToken(token)
                .map(PasswordResetToken::getEmail)
                .orElseThrow(() -> new TokenNotFoundException(TOKEN_NOT_FOUND));
    }

}
