package com.pigeon_stargram.sns_clone.service.search;

import com.pigeon_stargram.sns_clone.domain.search.SearchHistory;
import com.pigeon_stargram.sns_clone.domain.search.SearchTerm;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.search.internal.DeleteSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.internal.SaveSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseSearchHistoryDto;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseTopSearchDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;
import com.pigeon_stargram.sns_clone.repository.search.SearchHistoryRepository;
import com.pigeon_stargram.sns_clone.repository.search.SearchTermRepository;
import com.pigeon_stargram.sns_clone.service.user.UserBuilder;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.pigeon_stargram.sns_clone.service.search.SearchBuilder.*;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

    private final SearchTermRepository searchTermRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final UserService userService;

    public List<ResponseTopSearchDto> getTopSearchTermsByPrefix(String prefix) {
        return searchTermRepository.findTop5ByPrefixOrderByScoreDesc(prefix).stream()
                .filter(searchTerm -> !searchTerm.getTerm().equalsIgnoreCase(prefix))
                .map(SearchBuilder::buildResponseTopSearchDto)
                .toList();
    }

    public List<ResponseSearchHistoryDto> getTopSearchHistory(Long userId) {
        User user = userService.findById(userId);

        return searchHistoryRepository.findTop5ByUserOrderByModifiedDateDesc(user).stream()
                .map(SearchBuilder::buildResponseSearchHistoryDto)
                .toList();
    }

    public void deleteSearchHistory(DeleteSearchHistoryDto dto) {
        Long userId = dto.getUserId();
        String searchQuery = dto.getQuery();

        searchHistoryRepository.findByUserIdAndSearchQuery(userId, searchQuery)
                .ifPresent(searchHistoryRepository::delete);
    }

    public void deleteAllSearchHistory(Long userId) {

        List<SearchHistory> histories = searchHistoryRepository.findByUserId(userId);
        searchHistoryRepository.deleteAll(histories);
    }

    public void saveSearchHistory(SaveSearchHistoryDto dto) {
        Long userId = dto.getUserId();
        String searchQuery = dto.getSearchQuery();

        User user = userService.findById(userId);

        SearchHistory searchHistory =
                searchHistoryRepository.findByUserIdAndSearchQuery(userId, searchQuery)
                .map(history -> {
                    history.updateModifiedDate();
                    return history;
                })
                .orElseGet(() -> buildSearchHistory(user, searchQuery));

        searchHistoryRepository.save(searchHistory);

        updateSearchTermScores(searchQuery);
    }

    public void updateSearchTermScores(String term) {
        List<String> prefixes = generatePrefixes(term);

        prefixes.forEach(prefix -> {
            SearchTerm searchTerm = searchTermRepository.findByTermAndPrefix(term, prefix)
                    .orElse(buildSearchTerm(term, prefix));
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

    public List<ResponseUserInfoDto> getUserSearchResults(String searchQuery){

        return userService.findBySearchQuery(searchQuery).stream()
                .map(UserBuilder::buildResponseUserInfoDto)
                .toList();
    }

}
