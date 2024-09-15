package com.pigeon_stargram.sns_clone.service.recommend;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseRecommendUserInfoDto;
import com.pigeon_stargram.sns_clone.service.follow.FollowCrudService;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.dto.recommend.RecommendDtoConvertor.toResponseRecommendUserInfoDto;

/**
 * 친구 추천 로직을 처리하는 서비스 구현체입니다.
 * 사용자가 팔로우하지 않는 다른 사용자를 추천하고,
 * 추천된 사용자들의 추가 정보를 포함한 리스트를 반환합니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RecommendServiceV2 implements RecommendService {

    private final FollowCrudService followCrudService;
    private final FollowService followService;
    private final UserService userService;

    public List<ResponseRecommendUserInfoDto> recommendFriendsWithDetails(Long userId) {
        // 1. 추천 친구 목록 가져오기
        List<Long> recommendedUserIds = recommendFriends(userId);

        // 2. 추천 친구 각각에 대해 추가 정보를 구해서 DTO 리스트 만들기
        return recommendedUserIds.stream()
                .map(targetId -> {
                    // targetId를 팔로우하는 userId의 팔로잉 목록 중 몇 명이 targetId를 팔로우하는지 계산
                    List<Long> followingsWhoFollowTarget = followService.findFollowingsWhoFollowTarget(userId, targetId);

                    // 랜덤으로 팔로우하는 사람 하나를 선택
                    Long randomFollowerId = followingsWhoFollowTarget.get(ThreadLocalRandom.current().nextInt(followingsWhoFollowTarget.size()));
                    String randomRecommendName = userService.getUserById(randomFollowerId).getName();

                    // targetId의 사용자 정보 가져오기
                    User targetUser = userService.getUserById(targetId);

                    // DTO 생성
                    return toResponseRecommendUserInfoDto(targetUser,randomRecommendName,followingsWhoFollowTarget.size());
                })
                .collect(Collectors.toList());
    }

    public List<Long> recommendFriends(Long userId) {
        // 1. userId의 팔로잉 목록을 가져옴
        List<Long> userFollowingIds = followCrudService.findFollowingIds(userId);

        // 2. 팔로잉한 사람들의 팔로잉 목록에서 추천 대상 찾기
        Set<Long> recommendedUsers = userFollowingIds.stream()
                .flatMap(followingId -> followCrudService.findFollowingIds(followingId).stream())  // 팔로잉의 팔로잉 목록 가져옴
                .filter(recommendedId -> !userFollowingIds.contains(recommendedId) && !recommendedId.equals(userId))  // userId가 팔로우하지 않은 사람들만
                .collect(Collectors.toSet());  // 중복 제거

        // 3. 추천 대상 리스트로 변환
        List<Long> recommendedList = new ArrayList<>(recommendedUsers);

        // 4. 리스트를 무작위로 섞음
        Collections.shuffle(recommendedList);

        // 5. 최대 10명의 추천 대상을 반환
        return recommendedList.stream()
                .limit(10)  // 최대 10명 선택
                .collect(Collectors.toList());
    }

}
