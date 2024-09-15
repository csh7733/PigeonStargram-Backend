package com.pigeon_stargram.sns_clone.config.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * 세션 만료를 처리하는 인터셉터 클래스입니다.
 *
 * 이 클래스는 요청이 들어올 때 세션이 유효한지 검사하며,
 * 세션이 만료되었거나 로그인하지 않은 경우 401 Unauthorized 응답을 반환합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SessionExpiredInterceptor implements HandlerInterceptor {

    private final HttpSession httpSession;
    private final JsonUtil jsonUtil;

    /**
     * 요청 전 처리 메서드입니다.
     *
     * 세션에 저장된 사용자 정보를 확인하고, 세션이 유효하지 않다면 401 응답을 반환합니다.
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String requestUri = request.getRequestURI();

        // 세션에서 사용자 정보를 가져옴
        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        // 사용자 정보가 없으면 세션이 만료되었거나 로그인되지 않은 것으로 판단
        if (user == null) {
            // 응답 상태를 401 Unauthorized로 설정
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // 응답 메시지에 로그인 상태와 오류 메시지를 포함
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("isLoggedIn", false);

            // 세션이 새로 생성된 것인지 만료된 것인지에 따라 메시지 구분
            if (httpSession.isNew()) {
                responseBody.put("message", "User is not logged in.");
            } else {
                responseBody.put("message", "Session has expired.");
            }

            // 응답을 JSON 형식으로 변환하여 반환
            String jsonResponse = jsonUtil.toJson(responseBody);
            response.getWriter().write(jsonResponse);

            // 요청 처리 중단
            return false;
        }

        // 세션이 유효하면 요청을 계속 처리
        return true;
    }
}
