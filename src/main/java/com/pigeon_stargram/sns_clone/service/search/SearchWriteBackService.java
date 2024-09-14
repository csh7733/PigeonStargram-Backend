package com.pigeon_stargram.sns_clone.service.search;

import com.pigeon_stargram.sns_clone.domain.search.SearchHistory;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.search.SearchHistoryRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.pigeon_stargram.sns_clone.service.follow.FollowBuilder.buildFollow;
import static com.pigeon_stargram.sns_clone.service.search.SearchBuilder.buildSearchHistory;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SearchWriteBackService {

    private final RedisService redisService;
    private final UserService userService;
    private final SearchHistoryRepository searchHistoryRepository;
    public void syncSearchHistory(String key) {
        Long userId = RedisUtil.parseSuffix(key);
        log.info("WriteBack key={}", key);

        // userId로 사용자 조회
        User user = userService.getUserById(userId);

        // 캐시에서 검색 기록 모두 가져오기
        List<String> searchQueries = redisService.getAllFromSortedSet(key, String.class);

        // 캐시에서 가져온 검색 기록을 DB와 동기화
        for (String searchQuery : searchQueries) {
            // DB에서 userId와 searchQuery로 검색 기록 찾기, 없으면 새로 생성
            SearchHistory searchHistory = searchHistoryRepository.findByUserIdAndSearchQuery(userId, searchQuery)
                    .map(history -> {
                        // 존재하면 수정 시간 업데이트
                        Double score = redisService.getScoreFromSortedSet(key, searchQuery, Double.class);
                        history.updateScore(score);
                        return history;
                    })
                    .orElseGet(() -> buildSearchHistory(user, searchQuery)); // 존재하지 않으면 새로 생성

            // DB에 저장
            searchHistoryRepository.save(searchHistory);
        }
    }




}
