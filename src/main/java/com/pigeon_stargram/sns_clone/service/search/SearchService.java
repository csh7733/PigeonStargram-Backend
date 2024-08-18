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

import java.util.ArrayList;
import java.util.List;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getCurrentTime;

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

        return searchHistoryRepository.findTop5ByUserOrderByModifiedDateDesc(user).stream()
                .map(ResponseSearchHistoryDto::new)
                .toList();
    }

    public void deleteSearchHistory(Long userId, String searchQuery) {
        User user = userService.findById(userId);

        searchHistoryRepository.findByUserAndSearchQuery(user, searchQuery)
                .ifPresent(searchHistoryRepository::delete);
    }

    public void deleteAllSearchHistory(Long userId) {
        User user = userService.findById(userId);

        List<SearchHistory> histories = searchHistoryRepository.findByUser(user);
        searchHistoryRepository.deleteAll(histories);
    }

    public void saveSearchHistory(Long userId, String searchQuery) {
        User user = userService.findById(userId);

        SearchHistory searchHistory = searchHistoryRepository.findByUserAndSearchQuery(user, searchQuery)
                .map(history -> {
                    history.updateModifiedDate();
                    return history;
                })
                .orElseGet(() -> new SearchHistory(user, searchQuery));

        searchHistoryRepository.save(searchHistory);
    }

    public void updateSearchTermScores(String term) {
        List<String> prefixes = generatePrefixes(term);

        prefixes.forEach(prefix -> {
            SearchTerm searchTerm = searchTermRepository.findByTermAndPrefix(term, prefix)
                    .orElse(new SearchTerm(term, prefix));
            searchTerm.updateScore();
            searchTermRepository.save(searchTerm);
        });
    }

    private List<String> generatePrefixes(String term) {
        List<String> prefixes = new ArrayList<>();
        for (int i = 1; i <= term.length(); i++) {
            prefixes.add(term.substring(0, i));
        }
        return prefixes;
    }

}
