package com.pigeon_stargram.sns_clone.controller.search;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseTopSearchDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;
import com.pigeon_stargram.sns_clone.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.pigeon_stargram.sns_clone.dto.search.SearchDtoConvertor.*;

/**
 * 검색 기능과 관련된 API 요청을 처리하는 Controller 클래스입니다.
 * 검색 기록 관리, 자동 완성, 그리고 사용자 검색 결과 등을 처리합니다.
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final SearchService searchService;

    /**
     * 사용자가 입력한 prefix에 따라 자동 완성 검색 결과를 반환합니다.
     *
     * @param loginUser 현재 로그인한 사용자
     * @param prefix    검색어의 접두사
     * @return 자동 완성 검색 결과 리스트
     */
    @GetMapping("/auto-complete")
    public List<ResponseTopSearchDto> getAutoCompleteResults(@LoginUser SessionUser loginUser,
                                                             @RequestParam String prefix) {

        return searchService.getTopSearchTermsByPrefix(prefix);
    }

    /**
     * 사용자의 최근 검색 기록을 조회합니다.
     *
     * @param loginUser 현재 로그인한 사용자
     * @return 최근 검색 기록 리스트
     */
    @GetMapping("/history")
    public List<ResponseSearchHistoryDto> getSearchHistoryResults(@LoginUser SessionUser loginUser) {
        Long userId = loginUser.getId();

        return searchService.getTopSearchHistory(userId);
    }

    /**
     * 특정 검색 기록을 삭제합니다.
     *
     * @param loginUser 현재 로그인한 사용자
     * @param query     삭제할 검색어
     */
    @DeleteMapping("/history")
    public void deleteSearchHistory(@LoginUser SessionUser loginUser,
                                    @RequestParam String query) {
        Long userId = loginUser.getId();

        searchService.deleteSearchHistory(buildDeleteSearchHistoryDto(userId, query));
    }

    /**
     * 사용자의 모든 검색 기록을 삭제합니다.
     *
     * @param loginUser 현재 로그인한 사용자
     */
    @DeleteMapping("/history/all")
    public void deleteAllSearchHistory(@LoginUser SessionUser loginUser) {
        Long userId = loginUser.getId();

        searchService.deleteAllSearchHistory(userId);
    }

    /**
     * 검색어를 저장한 후, 사용자 검색 결과를 반환합니다.
     *
     * @param loginUser 현재 로그인한 사용자
     * @param query     검색어
     * @return 검색된 사용자 정보 리스트
     */
    @GetMapping
    public List<ResponseUserInfoDto> saveSearchInfosAndGetResults(@LoginUser SessionUser loginUser,
                                                                  @RequestParam String query) {
        Long userId = loginUser.getId();

        searchService.saveSearchHistory(buildSaveSearchHistoryDto(userId, query));

        return searchService.getUserSearchResults(query);
    }


}
