package com.pigeon_stargram.sns_clone.service.search;

import com.pigeon_stargram.sns_clone.domain.search.SearchHistory;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.search.SearchDtoConvertor;
import com.pigeon_stargram.sns_clone.dto.search.internal.DeleteSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.internal.SaveSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseTopSearchDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDtoConverter;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;
import com.pigeon_stargram.sns_clone.repository.search.SearchHistoryRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.dto.search.SearchDtoConvertor.*;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getCurrentTimeMillis;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

// 검색 기록 및 자동 완성 캐싱을 적용한 SearchServiceV2 구현체
// Value         | Structure | Key
// ------------- | --------- | ----------------------------------------
// search history| Sorted Set | SEARCH_HISTORY_USER_ID_{userId}          (사용자별 검색 기록)
// search term   | Sorted Set | SEARCH_TERM_SCORES_{prefix}              (접두사별 검색어 점수)
@Service
@RequiredArgsConstructor
@Transactional
public class SearchServiceV2 implements SearchService {

    private final UserService userService;
    private final RedisService redisService;

    private final SearchHistoryRepository searchHistoryRepository;

    public List<ResponseTopSearchDto> getTopSearchTermsByPrefix(String prefix) {
        String searchKey = cacheKeyGenerator(SEARCH_TERM_SCORES, prefix);

        // Redis에서 상위 5개의 검색어를 가져옴
        List<String> topSearchTerms = redisService.getTopNFromSortedSet(searchKey, 5, String.class);

        // prefix와 동일하지 않은 검색어만 필터링 후 DTO로 변환
        return topSearchTerms.stream()
                .filter(term -> !term.equalsIgnoreCase(prefix))
                .map(SearchDtoConvertor::buildResponseTopSearchDto)
                .collect(Collectors.toList());
    }

    public List<ResponseSearchHistoryDto> getTopSearchHistory(Long userId) {
        String cacheKey = cacheKeyGenerator(SEARCH_HISTORY, USER_ID, userId.toString());

        // 캐시에서 검색 기록을 조회
        List<ResponseSearchHistoryDto> cachedHistory = getCachedSearchHistory(cacheKey);
        if (!cachedHistory.isEmpty()) {
            return cachedHistory;  // 캐시에 검색 기록이 있으면 반환 (캐시 히트)
        }

        // 캐시에 데이터가 없으면 DB에서 검색 기록 조회 (캐시 미스)
        User user = userService.getUserById(userId);
        List<SearchHistory> searchHistories = searchHistoryRepository.findTop5ByUserOrderByScoreDesc(user);

        // 검색 기록을 캐시에 저장
        saveSearchHistoryToCache(cacheKey, searchHistories);

        // DB에서 가져온 검색 기록을 DTO로 변환하여 반환
        return searchHistories.stream()
                .map(SearchDtoConvertor::buildResponseSearchHistoryDto)
                .collect(Collectors.toList());
    }

    public void deleteSearchHistory(DeleteSearchHistoryDto dto) {
        Long userId = dto.getUserId();
        String searchQuery = dto.getQuery();

        // 사용자 검색 기록에 대한 캐시 키 생성
        String searchKey = cacheKeyGenerator(SEARCH_HISTORY, USER_ID, userId.toString());

        // Redis에서 해당 검색 기록 삭제
        redisService.removeFromSortedSet(searchKey, searchQuery);

        // 데이터베이스에서 해당 검색 기록 삭제
        searchHistoryRepository.findByUserIdAndSearchQuery(userId, searchQuery)
                .ifPresent(searchHistoryRepository::delete);
    }


    public void deleteAllSearchHistory(Long userId) {
        // 사용자 검색 기록에 대한 캐시 키 생성
        String searchKey = cacheKeyGenerator(SEARCH_HISTORY, USER_ID, userId.toString());

        // Redis에서 해당 사용자의 모든 검색 기록 삭제
        redisService.removeSortedSet(searchKey);

        // 데이터베이스에서 해당 사용자의 모든 검색 기록 삭제
        List<SearchHistory> histories = searchHistoryRepository.findByUserId(userId);
        searchHistoryRepository.deleteAll(histories);
    }


    public void saveSearchHistory(SaveSearchHistoryDto dto) {
        Long userId = dto.getUserId();
        String searchQuery = dto.getSearchQuery();

        // 사용자 검색 기록에 대한 캐시 키 생성
        String searchKey = cacheKeyGenerator(SEARCH_HISTORY, USER_ID, userId.toString());

        // 나중에 비동기적으로 DB에 flush 하도록 Write-back 작업을 위한 Sorted Set에 추가
        redisService.pushToWriteBackSortedSet(searchKey);

        // Redis에 검색 기록 추가 (Sorted Set에 추가)
        redisService.addToSortedSet(searchKey, getCurrentTimeMillis(), searchQuery, 3 * ONE_DAY_TTL);

        // 검색어 점수 업데이트 (검색어 자동 완성을 위한 점수 증가)
        updateSearchTermScores(searchQuery);
    }

    public void updateSearchTermScores(String term) {
        // 검색어의 접두사(prefix) 목록 생성
        List<String> prefixes = generatePrefixes(term);

        // 각 prefix를 기준으로 해당 검색어의 점수를 업데이트
        prefixes.forEach(prefix -> {
            // Redis에 저장할 캐시 키 생성
            String searchKey = cacheKeyGenerator(SEARCH_TERM_SCORES, prefix);
            // prefix를 키로 사용하고, 검색어를 값으로 하여 점수를 1 증가시킴
            redisService.incrementScoreInSortedSet(searchKey, term, 1.0);
        });
    }

    public List<ResponseUserInfoDto> getUserSearchResults(String searchQuery) {
        // 검색어를 기준으로 사용자 정보를 조회하고, Response DTO로 변환하여 반환
        return userService.findBySearchQuery(searchQuery).stream()
                .map(UserDtoConverter::toResponseUserInfoDto)
                .collect(Collectors.toList());
    }

    private List<ResponseSearchHistoryDto> getCachedSearchHistory(String cacheKey) {
        if (redisService.isSortedSetExists(cacheKey)) {
            // 캐시에서 상위 5개의 검색 기록을 가져옴
            List<Object> cachedSearchHistory = redisService.getTopNFromSortedSet(cacheKey, 5, Object.class);
            return cachedSearchHistory.stream()
                    .map(searchQuery -> {
                        // 검색 기록의 타임스탬프(score)를 가져와서 DTO로 변환
                        Double score = redisService.getScoreFromSortedSet(cacheKey, searchQuery, Double.class);
                        return buildResponseSearchHistoryDto(searchQuery.toString(), score);
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();  // 캐시에 검색 기록이 없으면 빈 리스트 반환
    }

    private void saveSearchHistoryToCache(String cacheKey, List<SearchHistory> searchHistories) {
        // 검색 기록을 Redis에 저장, TTL은 3일로 설정
        searchHistories.forEach(searchHistory ->
                redisService.addToSortedSet(cacheKey, searchHistory.getScore(), searchHistory.getSearchQuery(), 3 * ONE_DAY_TTL)
        );
    }

    private List<String> generatePrefixes(String term) {
        List<String> prefixes = new ArrayList<>();
        // 검색어의 길이에 따라 1글자씩 늘어나는 접두사(prefix) 목록 생성
        for (int i = 1; i <= term.length(); i++) {
            prefixes.add(term.substring(0, i));
        }
        return prefixes;
    }

}
