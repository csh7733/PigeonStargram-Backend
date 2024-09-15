package com.pigeon_stargram.sns_clone.service.recommend;

import com.pigeon_stargram.sns_clone.dto.user.response.ResponseRecommendUserInfoDto;

import java.util.List;

/**
 * 친구 추천과 관련된 서비스 인터페이스입니다.
 * 특정 사용자의 추천 친구 목록과 관련된 기능을 제공합니다.
 */
public interface RecommendService {

    /**
     * 추천된 친구들의 상세 정보를 포함한 리스트를 반환합니다.
     *
     * @param userId 현재 사용자 ID
     * @return 추천된 친구 목록 (ResponseRecommendUserInfoDto 리스트)
     */
    List<ResponseRecommendUserInfoDto> recommendFriendsWithDetails(Long userId);

    /**
     * 특정 사용자에게 추천할 친구 목록을 반환합니다.
     * 추천 대상은 사용자가 팔로우하지 않는 사람들로 구성됩니다.
     *
     * @param userId 현재 사용자 ID
     * @return 추천된 사용자 ID 리스트
     */
    List<Long> recommendFriends(Long userId);
}
