package com.pigeon_stargram.sns_clone.repository.search;

import com.pigeon_stargram.sns_clone.domain.search.SearchHistory;
import com.pigeon_stargram.sns_clone.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @Query("SELECT sh FROM SearchHistory sh WHERE sh.user = :user ORDER BY sh.modifiedDate DESC")
    List<SearchHistory> findTop5ByUserOrderByModifiedDateDesc(@Param("user") User user);

    Optional<SearchHistory> findByUserAndSearchQuery(User user, String SearchQuery);

    List<SearchHistory> findByUser(User user);
}
