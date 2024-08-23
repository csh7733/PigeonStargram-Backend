package com.pigeon_stargram.sns_clone.controller.search;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseTopSearchDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;
import com.pigeon_stargram.sns_clone.service.search.SearchBuilder;
import com.pigeon_stargram.sns_clone.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.pigeon_stargram.sns_clone.service.search.SearchBuilder.*;

@Slf4j
@RequestMapping("/api/search")
@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/auto-complete")
    public List<ResponseTopSearchDto> getAutoCompleteResults(@LoginUser SessionUser loginUser,
                                                             @RequestParam String prefix) {
        log.info("prefix = {}",prefix);
        return searchService.getTopSearchTermsByPrefix(prefix);
    }

    @GetMapping("/history")
    public List<ResponseSearchHistoryDto> getSearchHistoryResults(@LoginUser SessionUser loginUser) {
        Long userId = loginUser.getId();

        return searchService.getTopSearchHistory(userId);
    }

    @DeleteMapping("/history")
    public void deleteSearchHistory(@LoginUser SessionUser loginUser,
                                    @RequestParam String query) {
        Long userId = loginUser.getId();

        searchService.deleteSearchHistory(buildDeleteSearchHistoryDto(userId, query));
    }

    @DeleteMapping("/history/all")
    public void deleteAllSearchHistory(@LoginUser SessionUser loginUser) {
        Long userId = loginUser.getId();

        searchService.deleteAllSearchHistory(userId);
    }

    @GetMapping
    public List<ResponseUserInfoDto> saveSearchInfosAndGetResults(@LoginUser SessionUser loginUser,
                                                                  @RequestParam String query) {
        Long userId = loginUser.getId();

        searchService.saveSearchHistory(buildSaveSearchHistoryDto(userId, query));

        return searchService.getUserSearchResults(query);
    }


}
