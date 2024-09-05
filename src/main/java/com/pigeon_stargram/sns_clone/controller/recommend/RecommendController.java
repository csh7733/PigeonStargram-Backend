package com.pigeon_stargram.sns_clone.controller.recommend;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseRecommendUserInfoDto;
import com.pigeon_stargram.sns_clone.service.recommend.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    /**
     * 특정 사용자의 친구 추천 리스트를 반환
     *
     * @return 로그인 유저에게 추천된 사용자 리스트
     */
    @GetMapping
    public List<ResponseRecommendUserInfoDto> recommendFriends(@LoginUser SessionUser loginUser) {
        Long userId = loginUser.getId();

        return recommendService.recommendFriendsWithDetails(userId);
    }
}
