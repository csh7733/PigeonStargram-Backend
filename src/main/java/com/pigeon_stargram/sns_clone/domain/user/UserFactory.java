package com.pigeon_stargram.sns_clone.domain.user;

import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;

/**
 * 사용자 객체를 기반으로 세션 사용자 객체를 생성하는 팩토리 클래스입니다.
 */
public class UserFactory {

    /**
     * 주어진 사용자 객체를 사용하여 `SessionUser` 객체를 생성합니다.
     * <p>
     * 이 메서드는 `User` 객체의 ID, 이름, 이메일, 아바타를 기반으로 `SessionUser` 객체를 빌드합니다.
     * 세션 사용자 객체는 주로 인증 및 세션 관리를 위해 사용됩니다.
     * </p>
     * @param user `SessionUser`를 생성하기 위한 사용자 객체
     * @return 주어진 사용자 정보를 포함하는 `SessionUser` 객체
     */
    public static SessionUser createSessionUser(User user) {
        return SessionUser.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getWorkEmail())
                .picture(user.getAvatar())
                .build();
    }
}
