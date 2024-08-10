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

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final HttpSession httpSession;

    @Value("${oauth2.success-redirect-url}")
    private String successRedirectUrl;

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
