package com.pigeon_stargram.sns_clone.config.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2 인증 성공 후 처리하는 핸들러 클래스입니다.
 *
 * 이 클래스는 OAuth2 인증이 성공했을 때, 새로운 사용자 여부에 따라
 * 다른 리다이렉션 URL로 이동시키는 로직을 처리합니다.
 */
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final HttpSession httpSession;

    // 기존 회원 인증 성공 후 리다이렉션할 홈 URL
    @Value("${oauth2.success-redirect-url}")
    private String successRedirectUrl;


    // 신규 회원 인증 성공 후 리다이렉션할 회원가입 URL
    @Value("${oauth2.success-register-redirect-url}")
    private String registerRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Boolean isNewUser = (Boolean) httpSession.getAttribute("isNewUser");
        if (isNewUser != null && isNewUser) {
            response.sendRedirect(registerRedirectUrl);
        } else {
            response.sendRedirect(successRedirectUrl);
        }
    }
}
