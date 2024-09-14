package com.pigeon_stargram.sns_clone.dto.search;

import com.pigeon_stargram.sns_clone.domain.search.SearchHistory;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.search.internal.DeleteSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.internal.SaveSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseTopSearchDto;

import java.time.LocalDateTime;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_OPERATION;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.*;

/**
 * Search와 관련된 DTO 변환을 담당하는 유틸리티 클래스입니다.
 * 검색 기록, 검색어 등의 정보를 엔티티와 DTO 간에 변환하는 역할을 합니다.
 */
public class SearchDtoConvertor {

    private SearchDtoConvertor() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    /**
     * 검색어를 ResponseTopSearchDto로 변환합니다.
     *
     * @param searchTerm 검색어
     * @return 변환된 ResponseTopSearchDto 객체
     */
    public static ResponseTopSearchDto buildResponseTopSearchDto(String searchTerm) {
        return ResponseTopSearchDto.builder()
                .term(searchTerm)
                .build();
    }

    /**
     * SearchHistory 엔티티를 ResponseSearchHistoryDto로 변환합니다.
     *
     * @param searchHistory 검색 기록 엔티티
     * @return 변환된 ResponseSearchHistoryDto 객체
     */
    public static ResponseSearchHistoryDto buildResponseSearchHistoryDto(SearchHistory searchHistory) {
        LocalDateTime dateTime = convertDoubleToLocalDateTime(searchHistory.getScore());

        return ResponseSearchHistoryDto.builder()
                .searchQuery(searchHistory.getSearchQuery())
                .time(formatTime(dateTime))
                .build();
    }

    /**
     * 검색어와 점수를 ResponseSearchHistoryDto로 변환합니다.
     *
     * @param searchQuery 검색어
     * @param score       검색어의 점수
     * @return 변환된 ResponseSearchHistoryDto 객체
     */
    public static ResponseSearchHistoryDto buildResponseSearchHistoryDto(String searchQuery, Double score) {
        LocalDateTime dateTime = convertDoubleToLocalDateTime(score);

        return ResponseSearchHistoryDto.builder()
                .searchQuery(searchQuery)
                .time(formatTime(dateTime))
                .build();
    }

    /**
     * 검색 기록 삭제 요청을 위한 DeleteSearchHistoryDto를 생성합니다.
     *
     * @param userId 사용자 ID
     * @param query  삭제할 검색어
     * @return 생성된 DeleteSearchHistoryDto 객체
     */
    public static DeleteSearchHistoryDto buildDeleteSearchHistoryDto(Long userId,
                                                                     String query) {
        return DeleteSearchHistoryDto.builder()
                .userId(userId)
                .query(query)
                .build();
    }


    /**
     * 검색 기록 저장 요청을 위한 SaveSearchHistoryDto를 생성합니다.
     *
     * @param userId 사용자 ID
     * @param query  저장할 검색어
     * @return 생성된 SaveSearchHistoryDto 객체
     */
    public static SaveSearchHistoryDto buildSaveSearchHistoryDto(Long userId,
                                                                 String query) {
        return SaveSearchHistoryDto.builder()
                .userId(userId)
                .searchQuery(query)
                .build();
    }

}
