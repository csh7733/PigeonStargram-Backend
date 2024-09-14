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

import static com.pigeon_stargram.sns_clone.domain.search.SearchFactory.createSearchHistory;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SearchWriteBackService {

    private final RedisService redisService;
    private final UserService userService;
    private final SearchHistoryRepository searchHistoryRepository;

    /**
     * Redis 캐시된 검색 기록을 데이터베이스와 동기화합니다.
     *
     * @param key 사용자 검색 기록이 저장된 Redis 키
     */
    public void syncSearchHistory(String key) {
        Long userId = RedisUtil.parseSuffix(key);
        User user = userService.getUserById(userId);

        // 캐시에서 검색 기록 모두 가져오기
        List<String> searchQueries = redisService.getAllFromSortedSet(key, String.class);
        syncSearchHistoryToDB(user, key, searchQueries);
    }

    private void syncSearchHistoryToDB(User user, String redisKey, List<String> searchQueries) {
        for (String searchQuery : searchQueries) {
            // Redis에서 검색 기록의 Score(검색 날짜가 수치화된 값)을 가져옴
            Double score = redisService.getScoreFromSortedSet(redisKey, searchQuery, Double.class);
            
            // DB에서 기존 기록을 찾거나 새로 생성
            SearchHistory searchHistory = searchHistoryRepository.findByUserIdAndSearchQuery(user.getId(), searchQuery)
                    .map(history -> updateSearchHistoryScore(history, score))  // 기존 기록 업데이트
                    .orElseGet(() -> createSearchHistory(user, searchQuery, score));  // 없으면 새로 생성

            // DB에 저장
            searchHistoryRepository.save(searchHistory);
        }
    }

    private SearchHistory updateSearchHistoryScore(SearchHistory searchHistory, Double score) {
        // 최신 점수로 검색 기록의 점수 업데이트
        searchHistory.updateScore(score);
        return searchHistory;
    }

}
