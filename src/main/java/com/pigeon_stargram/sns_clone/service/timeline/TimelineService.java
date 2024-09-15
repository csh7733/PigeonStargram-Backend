package com.pigeon_stargram.sns_clone.service.timeline;

import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;

import java.util.List;

/**
 * 타임라인 관련 비즈니스 로직을 처리하는 Service 인터페이스.
 * 사용자의 팔로우 대상의 최근 게시물 조회 및 타임라인 관련 작업을 제공합니다.
 */
public interface TimelineService {

    /**
     * 사용자가 팔로우한 유명인과 비유명인의 최근 게시물을 조회하여 반환합니다.
     * 
     * @param userId 사용자 ID
     * @return 팔로우한 사용자들의 게시물을 시간순으로 정렬하여 반환
     */
    List<ResponsePostDto> getFollowingUsersRecentPosts(Long userId);

}
