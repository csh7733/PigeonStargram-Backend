package com.pigeon_stargram.sns_clone.repository.search;

import com.pigeon_stargram.sns_clone.domain.search.SearchTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchTermRepository extends JpaRepository<SearchTerm, Long> {
    @Query("SELECT st FROM SearchTerm st WHERE st.prefix = :prefix ORDER BY st.score DESC")
    List<SearchTerm> findTop5ByPrefixOrderByScoreDesc(String prefix);

    Optional<SearchTerm> findByTermAndPrefix(String term, String prefix);
}
