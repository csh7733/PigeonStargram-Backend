package com.pigeon_stargram.sns_clone.service.search;

import com.pigeon_stargram.sns_clone.domain.search.SearchHistory;
import com.pigeon_stargram.sns_clone.domain.search.SearchTerm;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.search.internal.DeleteSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.internal.SaveSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseTopSearchDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;
import com.pigeon_stargram.sns_clone.repository.search.SearchHistoryRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserBuilder;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.service.search.SearchBuilder.*;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getCurrentTimeMillis;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getTimeMillis;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserService userService;
    private final RedisService redisService;


    public List<ResponseTopSearchDto> getTopSearchTermsByPrefix(String prefix) {
        // 캐시 키 생성
        String searchKey = cacheKeyGenerator(SEARCH_TERM_SCORES, prefix);

        // Redis에서 상위 5개의 검색어를 점수순으로 가져옴
        List<String> topSearchTerms = redisService.getTopNFromSortedSet(searchKey, 5, String.class);

        // 검색어 목록을 DTO로 변환하여 반환
        return topSearchTerms.stream()
                .filter(term -> !term.equalsIgnoreCase(prefix)) // prefix와 동일하지 않은 검색어만 필터링
                .map(SearchBuilder::buildResponseTopSearchDto)  // ResponseTopSearchDto로 변환
                .collect(Collectors.toList());
    }


    public List<ResponseSearchHistoryDto> getTopSearchHistory(Long userId) {
        // 캐시 키 생성
        String cacheKey = cacheKeyGenerator(SEARCH_HISTORY, USER_ID, userId.toString());

        // Redis에서 해당 Sorted Set이 존재하는지 확인
        if (redisService.isSortedSetExists(cacheKey)) {
            // Sorted Set이 존재하면 상위 5개의 검색 기록을 가져옴 (캐시 히트)
            List<Object> cachedSearchHistory = redisService.getTopNFromSortedSet(cacheKey, 5, Object.class);

            // 캐시에서 가져온 검색 기록을 DTO로 변환하여 반환
            return cachedSearchHistory.stream()
                    .map(searchQuery -> {
                        Double score = redisService.getScoreFromSortedSet(cacheKey, searchQuery, Double.class); // 해당 검색어의 타임스탬프 가져오기
                        return buildResponseSearchHistoryDto(searchQuery.toString(), score);
                    })
                    .collect(Collectors.toList());
        }

        // Redis에 데이터가 없을 경우 (캐시 미스)
        User user = userService.findById(userId);
        List<SearchHistory> searchHistories = searchHistoryRepository.findTop5ByUserOrderByModifiedDateDesc(user);

        // DB에서 가져온 검색 기록을 Redis에 저장
        for (SearchHistory searchHistory : searchHistories) {
            redisService.addToSortedSet(cacheKey, getTimeMillis(searchHistory.getModifiedDate()), searchHistory.getSearchQuery());
        }

        // DB에서 가져온 검색 기록을 Response DTO로 변환하여 반환
        return searchHistories.stream()
                .map(SearchBuilder::buildResponseSearchHistoryDto)
                .collect(Collectors.toList());
    }


    public void deleteSearchHistory(DeleteSearchHistoryDto dto) {
        Long userId = dto.getUserId();
        String searchQuery = dto.getQuery();

        // 캐시 키 생성
        String searchKey = cacheKeyGenerator(SEARCH_HISTORY, USER_ID, userId.toString());

        // Redis에서 특정 검색 기록 삭제
        redisService.removeFromSortedSet(searchKey, searchQuery);

        // DB에서 특정 검색 기록 삭제
        searchHistoryRepository.findByUserIdAndSearchQuery(userId, searchQuery)
                .ifPresent(searchHistoryRepository::delete);
    }


    public void deleteAllSearchHistory(Long userId) {
        // 캐시 키 생성
        String searchKey = cacheKeyGenerator(SEARCH_HISTORY, USER_ID, userId.toString());

        // Redis에서 검색 기록 삭제
        redisService.removeSortedSet(searchKey);

        // DB에서 검색 기록 삭제
        List<SearchHistory> histories = searchHistoryRepository.findByUserId(userId);
        searchHistoryRepository.deleteAll(histories);
    }


    public void saveSearchHistory(SaveSearchHistoryDto dto) {
        Long userId = dto.getUserId();
        String searchQuery = dto.getSearchQuery();

        // DB에서 사용자 정보 조회
        User user = userService.findById(userId);

        // 캐시 키 생성
        String searchKey = cacheKeyGenerator(SEARCH_HISTORY, USER_ID, userId.toString());

        // Score를 위한 Double형태의 현재 시간값을 가져옴
        Double currentTimestamp = getCurrentTimeMillis();

        // Redis에 검색 기록 추가 (Sorted Set에 추가)
        redisService.addToSortedSet(searchKey, currentTimestamp, searchQuery);

        // DB에 검색 기록 저장 (write-through 방식)
        SearchHistory searchHistory =
                searchHistoryRepository.findByUserIdAndSearchQuery(userId, searchQuery)
                        .map(history -> {
                            history.updateModifiedDate();
                            return history;
                        })
                        .orElseGet(() -> buildSearchHistory(user, searchQuery));

        // DB에 저장
        searchHistoryRepository.save(searchHistory);

        // 검색어 점수 업데이트
        updateSearchTermScores(searchQuery);
    }



    public void updateSearchTermScores(String term) {
        // 검색어의 prefix 생성
        List<String> prefixes = generatePrefixes(term);

        // 각 prefix에 대해 term을 값으로 하여 점수 업데이트
        prefixes.forEach(prefix -> {
            // 캐시 키 생성
            String searchKey = cacheKeyGenerator(SEARCH_TERM_SCORES, prefix);
            // 각 prefix를 키로 사용하고, term을 값으로 점수를 증가시킴
            redisService.incrementScoreInSortedSet(searchKey, term, 1.0);
        });
    }


    private List<String> generatePrefixes(String term) {
        List<String> prefixes = new ArrayList<>();
        for (int i = 1; i <= term.length(); i++) {
            prefixes.add(term.substring(0, i));
        }
        return prefixes;
    }

    public List<ResponseUserInfoDto> getUserSearchResults(String searchQuery){

        return userService.findBySearchQuery(searchQuery).stream()
                .map(UserBuilder::buildResponseUserInfoDto)
                .collect(Collectors.toList());
    }

}
