package com.pigeon_stargram.sns_clone.domain.story;

import com.pigeon_stargram.sns_clone.domain.user.User;

/**
 * StoryFactory는 Story 엔티티 객체를 생성하는 역할을 담당합니다.
 */
public class StoryFactory {

    /**
     * 새로운 Story 엔티티 객체를 생성합니다.
     *
     * @param user 스토리를 작성한 사용자 객체
     * @param content 스토리의 텍스트 내용
     * @param img 스토리의 이미지 URL
     * @return 생성된 Story 엔티티 객체
     */
    public static Story createStory(User user, String content, String img) {
        return Story.builder()
                .user(user)
                .content(content)
                .img(img)
                .build();
    }
}
