package com.pigeon_stargram.sns_clone.service.login;

import com.pigeon_stargram.sns_clone.domain.login.PasswordResetToken;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestLoginDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestRegisterDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestResetPasswordDto;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdateOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdatePasswordDto;
import com.pigeon_stargram.sns_clone.exception.login.EmailNotSentException;
import com.pigeon_stargram.sns_clone.service.user.UserBuilder;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;
import static com.pigeon_stargram.sns_clone.service.login.LoginBuilder.buildSessionUser;
import static com.pigeon_stargram.sns_clone.service.login.LoginBuilder.buildUserInfoDto;
import static com.pigeon_stargram.sns_clone.service.user.UserBuilder.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class LoginService {

    private final JavaMailSender mailSender;

    private final UserService userService;

    private final PasswordResetTokenService passwordResetTokenService;

    private final HttpSession httpSession;

    @Value("${app.reset-password.url}")
    private String resetPasswordBaseUrl;

    public User findLoginUser(RequestLoginDto request) {
        String email = request.getEmail();
        String password = request.getPassword();

        return userService.findByWorkEmailAndPassword(email, password);
    }

    public void logout() {

        httpSession.invalidate();
    }

    /**
     * todo: 애플리케이션 레벨 예외처리 추가
     *      같은 요청 두개가 동시에 들어오는 경우 테스트
     */
    public void register(RequestRegisterDto request) {

        userService.save(request);
    }

    /**
     * TODO : 블로킹 비동기로 처리하기
     */
    public void sendPasswordResetLink(String email) {
        log.info("email = {}", email);

        userService.findByWorkEmail(email);

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
        } catch (MessagingException | MailException e) {
            throw new EmailNotSentException(EMAIL_NOT_SENT, e);
        }
    }

    public User resetPassword(RequestResetPasswordDto dto) {
        String token = dto.getToken();
        String newPassword = dto.getNewPassword();

        passwordResetTokenService.validateToken(token);

        String email = passwordResetTokenService.extractEmail(token);
        User user = userService.findByWorkEmail(email);
        UpdatePasswordDto updatePasswordDto =
                buildUpdatePasswordDto(user.getId(), newPassword);

        return userService.updatePassword(updatePasswordDto);
    }

    public ResponseEntity<?> login(RequestLoginDto dto) {
        User user = findLoginUser(dto);

        if (user != null) {
            log.info("login success");
            httpSession.setAttribute("user", buildSessionUser(user));
            return ResponseEntity.ok(buildUserInfoDto(user));
        } else {
            log.info("login fail");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

}
