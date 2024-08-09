package com.pigeon_stargram.sns_clone.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SessionExpiredInterceptor implements HandlerInterceptor {

    private final HttpSession httpSession;
    private final JsonUtil jsonUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("isLoggedIn", false);

            if (httpSession.isNew()) {
                responseBody.put("message", "User is not logged in.");
            } else {
                responseBody.put("message", "Session has expired.");
            }

            String jsonResponse = jsonUtil.toJson(responseBody);
            response.getWriter().write(jsonResponse);

            return false;
        }
        return true;
    }
}
