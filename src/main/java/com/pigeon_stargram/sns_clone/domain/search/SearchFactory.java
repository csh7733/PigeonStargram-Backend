package com.pigeon_stargram.sns_clone.domain.search;

import com.pigeon_stargram.sns_clone.domain.search.SearchHistory;
import com.pigeon_stargram.sns_clone.domain.user.User;

/**
 * SearchHistory 엔티티를 생성하는 팩토리 클래스입니다.
 */
public class SearchFactory {

    /**
     * 검색 기록을 생성하는 SearchHistory 엔티티를 빌드합니다.
     *
     * @param user        사용자
     * @param searchQuery 검색어
     * @return 생성된 SearchHistory 엔티티
     */
    public static SearchHistory createSearchHistory(User user, String searchQuery, Double score) {
        return SearchHistory.builder()
                .user(user)
                .searchQuery(searchQuery)
                .score(score)
                .build();
    }
}
