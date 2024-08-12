package com.pigeon_stargram.sns_clone.service.login;

import com.pigeon_stargram.sns_clone.domain.login.PasswordResetToken;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.login.request.LoginDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RegisterDto;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LoginService {

    private final JavaMailSender mailSender;

    private final UserService userService;

    private final PasswordResetTokenService passwordResetTokenService;

    private final HttpSession httpSession;

    @Value("${app.reset-password.url}")
    private String resetPasswordBaseUrl;

    public User login(LoginDto request) {
        String email = request.getEmail();
        String password = request.getPassword();
        return userService.findByWorkEmailAndPassword(email,password);
    }

    public void logout() {
        httpSession.invalidate();
    }

    public void register(RegisterDto request) {
        userService.save(request);
    }

    /**
     * TODO : 블로킹 비동기로 처리하기
     */
    public Boolean sendPasswordResetLink(String email) {
        log.info("email = {}",email);
        User user = userService.findByEmail(email);
        if (user == null) {
            return false;
        }

        try {
            PasswordResetToken resetToken = passwordResetTokenService.createToken(email);
            String resetPasswordLink = resetPasswordBaseUrl + resetToken.getToken();

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            String body = new StringBuilder()
                    .append("<div style=\"font-family: Arial, sans-serif; font-size: 16px; color: #333; max-width: 600px; margin: 0 auto;\">")
                    .append("<h2 style=\"color: #4CAF50; text-align: center;\">PigeonStargram 비밀번호 재설정</h2>")
                    .append("<p>안녕하세요, PigeonStargram입니다.</p>")
                    .append("<p>아래 버튼을 눌러 비밀번호를 재설정하실 수 있습니다. 비밀번호 재설정 후 다시 로그인해 주세요.</p>")
                    .append("<div style=\"text-align: center; margin: 20px 0;\">")
                    .append("<a href=\"").append(resetPasswordLink).append("\" ")
                    .append("style=\"display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; font-weight: bold; border-radius: 5px;\">")
                    .append("비밀번호 재설정</a>")
                    .append("</div>")
                    .append("<p style=\"text-align: center;\">혹시 버튼이 작동하지 않을 경우, 아래 링크를 복사하여 브라우저에 붙여넣어 주세요:</p>")
                    .append("<p style=\"text-align: center; word-break: break-all; margin-bottom: 20px;\">")
                    .append("<a href=\"").append(resetPasswordLink).append("\" style=\"color: #4CAF50;\">")
                    .append(resetPasswordLink).append("</a>")
                    .append("</p>")
                    .append("<p style=\"margin-bottom: 20px;\">감사합니다.</p>")
                    .append("<p style=\"margin-bottom: 0;\">PigeonStargram Team</p>")
                    .append("</div>")
                    .toString();

            helper.setTo(email);
            helper.setSubject("PigeonStargram - 비밀번호 재설정 요청");
            helper.setText(body, true);

            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean validateToken(String token) {
        return Optional.of(passwordResetTokenService.validateToken(token))
                .filter(isValid -> isValid)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));
    }

    public void resetPassword(String token, String newPassword) {
        String email = Optional.ofNullable(passwordResetTokenService.extractEmail(token))
                .orElseThrow(() -> new IllegalArgumentException("Invalid token or email not found"));

        User user = Optional.ofNullable(userService.findByEmail(email))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userService.updatePassword(user, newPassword);
    }

}
