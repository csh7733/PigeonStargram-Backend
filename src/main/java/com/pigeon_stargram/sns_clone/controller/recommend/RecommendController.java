package com.pigeon_stargram.sns_clone.controller.recommend;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseRecommendUserInfoDto;
import com.pigeon_stargram.sns_clone.service.recommend.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 친구 추천과 관련된 API 요청을 처리하는 Controller 클래스입니다.
 * 사용자가 팔로우할 만한 친구들을 추천하는 기능을 제공합니다.
 * 추천 서비스와 연동하여 친구 추천 목록을 불러옵니다.
 */
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    /**
     * 특정 사용자의 친구 추천 리스트를 반환
     *
     * @param loginUser 현재 로그인한 사용자 (세션 정보)
     * @return 로그인 유저에게 추천된 사용자 리스트
     */
    @GetMapping
    public List<ResponseRecommendUserInfoDto> recommendFriends(@LoginUser SessionUser loginUser) {
        Long userId = loginUser.getId();

        return recommendService.recommendFriendsWithDetails(userId);
    }
}
