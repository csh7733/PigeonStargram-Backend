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
import static com.pigeon_stargram.sns_clone.domain.user.UserFactory.createSessionUser;
import static com.pigeon_stargram.sns_clone.dto.user.UserDtoConverter.toUpdatePasswordDto;
import static com.pigeon_stargram.sns_clone.dto.user.UserDtoConverter.toUserInfoDto;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.EMAIL_MISMATCH;

/**
 * 사용자 로그인 및 관련 기능을 제공하는 서비스 클래스입니다.
 * <p>
 * 이 서비스는 사용자 등록, 로그인, 로그아웃, 비밀번호 재설정 요청 및 처리를 포함합니다.
 * </p>
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserService userService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final RedisService redisService;

    private final HttpSession httpSession;

    @Value("${app.reset-password.url}")
    private String resetPasswordBaseUrl;

    /**
     * 비밀번호 재설정 이메일의 HTML 본문을 생성합니다.
     *
     * @param resetPasswordLink 비밀번호 재설정 링크
     * @return HTML 형식의 이메일 본문
     */
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

    /**
     * 로그인 요청을 처리하여 사용자 정보를 반환합니다.
     * <p>
     * 주어진 이메일과 비밀번호를 사용하여 사용자를 조회하고, 로그인에 성공하면 사용자 객체를 반환합니다.
     * </p>
     *
     * @param request 로그인 요청 DTO
     * @return 로그인 성공 시 사용자 객체
     */
    public User findLoginUser(RequestLoginDto request) {
        String email = request.getEmail();
        String password = request.getPassword();

        return userService.getUserByWorkEmailAndPassword(email, password);
    }

    /**
     * 현재 사용자의 세션을 무효화하여 로그아웃합니다.
     */
    public void logout() {
        httpSession.invalidate();
    }

    /**
     * 사용자를 등록합니다.
     * <p>
     * 이메일과 등록 요청 DTO를 검증한 후 사용자 정보를 데이터베이스에 저장합니다.
     * </p>
     *
     * @param email   사용자 등록 요청 이메일 주소
     * @param request 사용자 등록 요청 DTO
     */
    public void register(String email,
                         RequestRegisterDto request) {
        // 등록 요청의 이메일과 실제 이메일이 일치하는지 검증
        verifyEmail(email, request);

        // 사용자 정보를 데이터베이스에 저장
        userService.save(request);
    }

    /**
     * 비밀번호 재설정 링크를 사용자의 이메일로 전송합니다.
     * <p>
     * 이메일을 통해 사용자 계정을 확인한 후, 비밀번호 재설정 토큰을 생성하고 링크를 포함한 이메일을 Redis 작업 큐에 추가합니다.
     * </p>
     *
     * @param email 비밀번호 재설정을 요청한 이메일 주소
     */
    public void sendPasswordResetLink(String email) {
        // 이메일로 사용자 존재 여부 확인
        userService.getUserByWorkEmail(email);

        // 비밀번호 재설정 토큰 생성
        PasswordResetToken resetToken = passwordResetTokenService.createToken(email);
        String resetPasswordLink = resetPasswordBaseUrl + resetToken.getToken();

        // 비밀번호 재설정 링크를 포함한 이메일 본문 생성
        String body = getBody(resetPasswordLink);

        String subject = "PigeonStargram - 비밀번호 재설정 요청";
        MailTask mailTask = new MailTask(email, subject, body);

        // Redis 작업 큐에 메일 전송 작업 추가
        redisService.pushTask(MAIL_QUEUE, mailTask);
    }

    /**
     * 비밀번호를 재설정합니다.
     * <p>
     * 비밀번호 재설정 토큰을 검증하고, 이메일을 추출하여 사용자 정보를 조회한 후 비밀번호를 업데이트합니다.
     * </p>
     *
     * @param request 비밀번호 재설정 요청 DTO
     * @return 비밀번호가 성공적으로 재설정된 사용자 객체
     */
    public User resetPassword(RequestResetPasswordDto request) {
        // 비밀번호 재설정 토큰 검증
        String token = request.getToken();
        String newPassword = request.getNewPassword();
        passwordResetTokenService.validateToken(token);

        // 토큰에서 이메일 추출
        String email = passwordResetTokenService.extractEmail(token);
        // 이메일로 사용자 조회
        User user = userService.getUserByWorkEmail(email);

        // 비밀번호 업데이트
        UpdatePasswordDto dto = toUpdatePasswordDto(user.getId(), newPassword);
        return userService.updatePassword(dto);
    }

    /**
     * 사용자를 로그인하고 세션에 사용자 정보를 설정합니다.
     * <p>
     * 로그인 성공 시 사용자 정보를 세션에 저장하고, 사용자 정보를 DTO로 반환합니다. 실패 시 UNAUTHORIZED 상태 반환.
     * </p>
     *
     * @param dto 로그인 요청 DTO
     * @return 로그인 성공 시 사용자 정보 DTO, 실패 시 UNAUTHORIZED 상태
     */
    public ResponseEntity<?> login(RequestLoginDto dto) {
        User user = findLoginUser(dto);

        if (user != null) {
            // 세션에 사용자 정보 저장
            httpSession.setAttribute("user", createSessionUser(user));
            return ResponseEntity.ok(toUserInfoDto(user));
        } else {
            // 로그인 실패 시 UNAUTHORIZED 상태 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    /**
     * 사용자 등록 요청의 이메일 주소와 입력된 이메일 주소가 일치하는지 검증합니다.
     *
     * @param email   사용자가 등록을 요청한 이메일 주소
     * @param request 사용자 등록 요청 DTO
     * @throws EmailMismatchException 이메일 주소 불일치 예외
     */
    private void verifyEmail(String email, RequestRegisterDto request) {
        String registerEmail = request.getEmail();
        if (!email.equals(registerEmail)) {
            throw new EmailMismatchException(EMAIL_MISMATCH);
        }
    }

}
