package com.pigeon_stargram.sns_clone.service.search;

import com.pigeon_stargram.sns_clone.dto.search.internal.DeleteSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.internal.SaveSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseTopSearchDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;

import java.util.List;

/**
 * 검색 기능과 관련된 비즈니스 로직을 처리하는 Service 인터페이스입니다.
 * 검색어 자동 완성, 검색 기록 조회 및 삭제, 검색어 저장 등 다양한 기능을 제공합니다.
 */
public interface SearchService {

    /**
     * 특정 접두사(prefix)를 기준으로 상위 검색어를 가져옵니다.
     *
     * @param prefix 검색어 접두사
     * @return 상위 검색어 목록
     */
    List<ResponseTopSearchDto> getTopSearchTermsByPrefix(String prefix);

    /**
     * 사용자의 최근 검색 기록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 최근 검색 기록 목록
     */
    List<ResponseSearchHistoryDto> getTopSearchHistory(Long userId);

    /**
     * 특정 검색 기록을 삭제합니다.
     *
     * @param dto 삭제할 검색 기록 정보가 담긴 DTO
     */
    void deleteSearchHistory(DeleteSearchHistoryDto dto);

    /**
     * 사용자의 모든 검색 기록을 삭제합니다.
     *
     * @param userId 사용자 ID
     */
    void deleteAllSearchHistory(Long userId);

    /**
     * 검색 기록을 저장합니다.
     *
     * @param dto 저장할 검색 기록 정보가 담긴 DTO
     */
    void saveSearchHistory(SaveSearchHistoryDto dto);

    /**
     * 검색어의 점수를 업데이트합니다.
     *
     * @param term 검색어
     */
    void updateSearchTermScores(String term);

    /**
     * 특정 검색어에 대한 사용자 검색 결과를 반환합니다.
     *
     * @param searchQuery 검색어
     * @return 검색된 사용자 정보 리스트
     */
    List<ResponseUserInfoDto> getUserSearchResults(String searchQuery);
}
