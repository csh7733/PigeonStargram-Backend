package com.pigeon_stargram.sns_clone.repository.search;

import com.pigeon_stargram.sns_clone.domain.search.SearchHistory;
import com.pigeon_stargram.sns_clone.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistory> findTop5ByUserOrderByModifiedDateDesc(User user);

    Optional<SearchHistory> findByUserIdAndSearchQuery(Long userId, String SearchQuery);

    List<SearchHistory> findByUserId(Long userId);
}
