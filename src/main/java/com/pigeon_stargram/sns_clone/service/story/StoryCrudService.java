package com.pigeon_stargram.sns_clone.service.story;

import com.pigeon_stargram.sns_clone.domain.story.Story;

/**
 * 스토리 관련 비즈니스 로직을 처리하는 Service 인터페이스.
 * 스토리 등록, 조회, 삭제 등의 메서드를 제공합니다.
 */
public interface StoryCrudService {

    /**
     * 새로운 스토리를 저장합니다.
     *
     * @param story 저장할 Story 객체
     * @return 저장된 Story 객체
     */
    Story save(Story story);

    /**
     * ID를 기반으로 스토리를 조회합니다.
     *
     * @param storyId 조회할 스토리의 ID
     * @return 조회된 Story 객체
     */
    Story findById(Long storyId);

    /**
     * ID를 기반으로 스토리를 삭제합니다.
     *
     * @param storyId 삭제할 스토리의 ID
     */
    void delete(Long storyId);
}
