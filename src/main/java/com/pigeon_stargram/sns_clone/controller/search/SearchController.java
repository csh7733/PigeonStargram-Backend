package com.pigeon_stargram.sns_clone.controller.search;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseTopSearchDto;
import com.pigeon_stargram.sns_clone.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}
