package com.pigeon_stargram.sns_clone.config.auth.service;

import com.pigeon_stargram.sns_clone.config.auth.dto.OAuthAttributes;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.exception.user.UserNotFoundException;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.pigeon_stargram.sns_clone.domain.user.UserFactory.createSessionUser;

/**
 * OAuth2 사용자 정보를 처리하는 서비스 클래스입니다.
 *
 * 이 클래스는 OAuth2 인증 후 사용자 정보를 가져와 기존 사용자인지 확인하고,
 * 새로운 사용자라면 세션에 사용자 이메일과 상태를 저장하며,
 * 기존 사용자라면 세션에 사용자 정보를 저장합니다.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 기본 OAuth2 사용자 서비스로부터 사용자 정보를 로드
        OAuth2UserService<OAuth2UserRequest, OAuth2User>
                delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);


        // 클라이언트 등록 정보와 사용자 이름 속성 이름을 가져옴
        String registrationId = userRequest.getClientRegistration()
                .getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // OAuth2 사용자 정보를 OAuthAttributes 객체로 변환
        OAuthAttributes attributes = OAuthAttributes
                .of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 사용자 정보가 존재하지 않으면 새로운 사용자로 처리
        User user = findOrCreateUser(attributes.getEmail());
        if (user == null) {
            return handleNewUser(attributes);  // 새로운 사용자 처리
        } else {
            return handleExistingUser(user, attributes); // 기존 사용자 처리
        }
    }

    /**
     * 이메일을 기반으로 사용자 정보를 찾고, 존재하지 않으면 null을 반환합니다.
     *
     * @param email 사용자의 이메일 주소
     * @return User 사용자 정보 객체, 없으면 null 반환
     */
    private User findOrCreateUser(String email) {
        try {
            return userService.getUserByWorkEmail(email);
        } catch (UserNotFoundException e) {
            return null;
        }
    }

    /**
     * 새로운 사용자를 처리하는 메서드입니다.
     *
     * 세션에 새로운 사용자 상태와 이메일을 저장한 후, 기본 ROLE_USER 권한을 부여합니다.
     *
     * @param attributes OAuth2 사용자 정보 객체
     * @return OAuth2User 인증된 새로운 사용자 정보 객체
     */
    private OAuth2User handleNewUser(OAuthAttributes attributes) {
        httpSession.setAttribute("isNewUser", true);
        httpSession.setAttribute("email", attributes.getEmail());
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    /**
     * 기존 사용자를 처리하는 메서드입니다.
     *
     * 세션에 사용자 정보를 저장한 후, 사용자의 권한에 따라 인증된 사용자 정보를 반환합니다.
     *
     * @param user 기존 사용자 정보 객체
     * @param attributes OAuth2 사용자 정보 객체
     * @return OAuth2User 인증된 기존 사용자 정보 객체
     */
    private OAuth2User handleExistingUser(User user, OAuthAttributes attributes) {
        httpSession.setAttribute("user", createSessionUser(user));
        httpSession.setAttribute("isNewUser", false);
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }
}
