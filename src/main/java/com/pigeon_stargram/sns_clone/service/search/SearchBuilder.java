package com.pigeon_stargram.sns_clone.service.search;

import com.pigeon_stargram.sns_clone.domain.search.SearchHistory;
import com.pigeon_stargram.sns_clone.domain.search.SearchTerm;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.search.internal.DeleteSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.internal.SaveSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseTopSearchDto;
import com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_OPERATION;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.*;

public class SearchBuilder {

    private SearchBuilder() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    public static ResponseTopSearchDto buildResponseTopSearchDto(SearchTerm searchTerm) {
        return ResponseTopSearchDto.builder()
                .term(searchTerm.getTerm())
                .build();
    }

    public static ResponseSearchHistoryDto buildResponseSearchHistoryDto(SearchHistory searchHistory) {
        return ResponseSearchHistoryDto.builder()
                .searchQuery(searchHistory.getSearchQuery())
                .time(formatTime(searchHistory.getModifiedDate()))
                .build();
    }

    public static ResponseSearchHistoryDto buildResponseSearchHistoryDto(String searchQuery, Double score) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(score.longValue()), ZoneId.systemDefault());

        return ResponseSearchHistoryDto.builder()
                .searchQuery(searchQuery)
                .time(formatTime(dateTime))
                .build();
    }

    public static DeleteSearchHistoryDto buildDeleteSearchHistoryDto(Long userId,
                                                                     String query) {
        return DeleteSearchHistoryDto.builder()
                .userId(userId)
                .query(query)
                .build();
    }

    public static SaveSearchHistoryDto buildSaveSearchHistoryDto(Long userId,
                                                                 String query) {
        return SaveSearchHistoryDto.builder()
                .userId(userId)
                .searchQuery(query)
                .build();
    }

    public static SearchHistory buildSearchHistory(User user,
                                                   String searchQuery) {
        return SearchHistory.builder()
                .user(user)
                .searchQuery(searchQuery)
                .build();
    }

    public static SearchTerm buildSearchTerm(String term,
                                             String prefix) {
        return SearchTerm.builder()
                .prefix(prefix)
                .term(term)
                .score(0L)
                .build();

    }

}
