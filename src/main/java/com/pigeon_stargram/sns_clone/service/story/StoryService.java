package com.pigeon_stargram.sns_clone.service.story;

import com.pigeon_stargram.sns_clone.domain.story.Story;
import com.pigeon_stargram.sns_clone.dto.story.internal.GetRecentStoriesDto;
import com.pigeon_stargram.sns_clone.dto.story.internal.MarkStoryAsViewedDto;
import com.pigeon_stargram.sns_clone.dto.story.internal.UploadStoryDto;
import com.pigeon_stargram.sns_clone.dto.story.response.ResponseStoriesDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;

import java.util.List;

/**
 * 스토리 관련 비즈니스 로직을 처리하는 Service 인터페이스.
 * 스토리 업로드, 삭제, 조회, 읽음 처리 등 다양한 스토리 관련 기능을 제공합니다.
 */
public interface StoryService {

    /**
     * 새로운 스토리를 업로드합니다.
     *
     * @param dto 스토리 업로드를 위한 DTO
     * @return 업로드된 Story 객체
     */
    Story uploadStory(UploadStoryDto dto);

    /**
     * 스토리를 삭제합니다.
     *
     * @param storyId 삭제할 스토리의 ID
     */
    void deleteStory(Long storyId);

    /**
     * 사용자가 특정 스토리를 조회했음을 기록합니다.
     *
     * @param dto 스토리 조회 기록을 위한 DTO
     */
    void markStoryAsViewed(MarkStoryAsViewedDto dto);

    /**
     * 사용자의 최근 스토리 목록을 가져옵니다.
     *
     * @param dto 스토리 조회를 위한 DTO
     * @return 최근 스토리와 마지막으로 읽은 스토리 인덱스를 포함한 ResponseStoriesDto 객체
     */
    ResponseStoriesDto getRecentStories(GetRecentStoriesDto dto);

    /**
     * 특정 스토리를 조회한 사용자들의 정보를 반환합니다.
     *
     * @param storyId 스토리의 ID
     * @return 조회한 사용자들의 정보 리스트
     */
    List<ResponseUserInfoDto> getUserInfosWhoViewedStory(Long storyId);

    /**
     * 사용자가 특정 스토리를 조회했는지 확인합니다.
     *
     * @param storyId 스토리의 ID
     * @param userId 사용자의 ID
     * @return 조회 여부
     */
    Boolean hasUserViewedStory(Long storyId, Long userId);

    /**
     * 사용자가 최근 스토리를 가지고 있는지 확인합니다.
     *
     * @param userId 사용자의 ID
     * @return 최근 스토리 보유 여부
     */
    Boolean hasRecentStory(Long userId);

    /**
     * 사용자가 읽지 않은 스토리가 있는지 확인합니다.
     *
     * @param userId 사용자의 ID
     * @param currentMemberId 현재 사용자의 ID
     * @return 읽지 않은 스토리 여부
     */
    Boolean hasUnreadStories(Long userId, Long currentMemberId);
}
