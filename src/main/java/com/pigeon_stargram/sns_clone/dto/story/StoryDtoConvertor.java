package com.pigeon_stargram.sns_clone.dto.story;

import com.pigeon_stargram.sns_clone.domain.story.Story;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.story.internal.GetRecentStoriesDto;
import com.pigeon_stargram.sns_clone.dto.story.internal.MarkStoryAsViewedDto;
import com.pigeon_stargram.sns_clone.dto.story.internal.UploadStoryDto;

/**
 * Story 객체와 다양한 DTO 간의 변환을 담당하는 클래스입니다.
 * 스토리 업로드, 조회, 조회 기록과 관련된 DTO로 변환하는 메서드를 제공합니다.
 */
public class StoryDtoConvertor {

    /**
     * 스토리 업로드를 위한 정보를 담은 UploadStoryDto를 생성합니다.
     *
     * @param userId 스토리를 업로드하는 사용자의 ID
     * @param content 스토리의 텍스트 내용
     * @param imageUrl 업로드된 이미지의 URL
     * @return 변환된 UploadStoryDto
     */
    public static UploadStoryDto buildUploadStoryDto(Long userId, String content, String imageUrl) {
        return UploadStoryDto.builder()
                .userId(userId)
                .content(content)
                .imageUrl(imageUrl)
                .build();
    }


    /**
     * 사용자가 스토리를 조회했음을 기록하기 위한 MarkStoryAsViewedDto를 생성합니다.
     *
     * @param storyId 조회한 스토리의 ID
     * @param userId 스토리를 조회한 사용자의 ID
     * @return 변환된 MarkStoryAsViewedDto
     */
    public static MarkStoryAsViewedDto buildMarkStoryAsViewedDto(Long storyId, Long userId) {
        return MarkStoryAsViewedDto.builder()
                .storyId(storyId)
                .userId(userId)
                .build();
    }

    /**
     * 특정 사용자의 최근 스토리를 조회하기 위한 GetRecentStoriesDto를 생성합니다.
     *
     * @param userId 스토리를 조회할 사용자의 ID
     * @param currentMemberId 현재 로그인한 사용자의 ID
     * @return 변환된 GetRecentStoriesDto
     */
    public static GetRecentStoriesDto buildGetRecentStoriesDto(Long userId, Long currentMemberId) {
        return GetRecentStoriesDto.builder()
                .userId(userId)
                .currentMemberId(currentMemberId)
                .build();
    }

}
