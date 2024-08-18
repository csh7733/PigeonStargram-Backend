package com.pigeon_stargram.sns_clone.service.search;

import com.pigeon_stargram.sns_clone.domain.search.SearchHistory;
import com.pigeon_stargram.sns_clone.domain.search.SearchTerm;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseTopSearchDto;
import com.pigeon_stargram.sns_clone.repository.search.SearchHistoryRepository;
import com.pigeon_stargram.sns_clone.repository.search.SearchTermRepository;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

    private final SearchTermRepository searchTermRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final UserService userService;

    public List<ResponseTopSearchDto> getTopSearchTermsByPrefix(String prefix) {
        return searchTermRepository.findTop5ByPrefixOrderByScoreDesc(prefix).stream()
                .map(ResponseTopSearchDto::new)
                .toList();
    }

    public List<ResponseSearchHistoryDto> getTopSearchHistory(Long userId) {
        User user = userService.findById(userId);

        return searchHistoryRepository.findTop5ByUserOrderByCreatedDateDesc(user).stream()
                .map(ResponseSearchHistoryDto::new)
                .toList();
    }

}
