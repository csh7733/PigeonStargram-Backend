package com.pigeon_stargram.sns_clone.repository.search;

import com.pigeon_stargram.sns_clone.domain.search.SearchHistory;
import com.pigeon_stargram.sns_clone.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @Query("SELECT sh FROM SearchHistory sh WHERE sh.user = :user ORDER BY sh.createdDate DESC")
    List<SearchHistory> findTop5ByUserOrderByCreatedDateDesc(@Param("user") User user);
}
