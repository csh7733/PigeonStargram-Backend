package com.pigeon_stargram.sns_clone.service.login;

import com.pigeon_stargram.sns_clone.domain.login.PasswordResetToken;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.login.internal.MailTask;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestLoginDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestRegisterDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestResetPasswordDto;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdatePasswordDto;
import com.pigeon_stargram.sns_clone.exception.login.EmailMismatchException;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.pigeon_stargram.sns_clone.constant.RedisQueueConstants.MAIL_QUEUE;
import static com.pigeon_stargram.sns_clone.dto.user.UserDtoConverter.*;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;
import static com.pigeon_stargram.sns_clone.service.login.LoginBuilder.buildSessionUser;
import static com.pigeon_stargram.sns_clone.service.login.LoginBuilder.buildUserInfoDto;
import static com.pigeon_stargram.sns_clone.util.LogUtil.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class LoginService {

    private final UserService userService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final HttpSession httpSession;
    private final RedisService redisService;

    @Value("${app.reset-password.url}")
    private String resetPasswordBaseUrl;

    public User findLoginUser(RequestLoginDto request) {
        String email = request.getEmail();
        String password = request.getPassword();

        return userService.getUserByWorkEmailAndPassword(email, password);
    }

    public void logout() {

        httpSession.invalidate();
    }

    /**
     * 사용자 등록을 처리합니다.
     *
     * @param email 사용자가 등록을 요청한 이메일 주소
     * @param request 사용자 등록 요청에 대한 DTO 객체
     */
    public void register(String email,
                         RequestRegisterDto request) {
        logServiceMethod("register", email, request);

        String registerEmail = request.getEmail();
        if(!email.equals(registerEmail)){
            throw new EmailMismatchException(EMAIL_MISMATCH);
        }

        userService.save(request);
    }

    public void sendPasswordResetLink(String email) {
        log.info("email = {}", email);

        userService.getUserByWorkEmail(email);

        PasswordResetToken resetToken = passwordResetTokenService.createToken(email);
        String resetPasswordLink = resetPasswordBaseUrl + resetToken.getToken();

        String body = getBody(resetPasswordLink);

        String subject = "PigeonStargram - 비밀번호 재설정 요청";
        MailTask mailTask = new MailTask(email, subject, body);

        // Redis 작업큐에 task를 추가
        redisService.pushTask(MAIL_QUEUE, mailTask);
    }

    /**
     * 비밀번호 재설정을 처리합니다.
     *
     * @param request 비밀번호 재설정 요청에 대한 DTO 객체
     * @return 비밀번호가 성공적으로 재설정된 사용자 객체
     */
    public User resetPassword(RequestResetPasswordDto request) {
        logServiceMethod("resetPassword", request);

        String token = request.getToken();
        String newPassword = request.getNewPassword();

        passwordResetTokenService.validateToken(token);

        String email = passwordResetTokenService.extractEmail(token);
        User user = userService.getUserByWorkEmail(email);

        UpdatePasswordDto dto = toUpdatePasswordDto(user.getId(), newPassword);
        return userService.updatePassword(dto);
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

    private static String getBody(String resetPasswordLink) {
        return new StringBuilder()
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
    }


}
