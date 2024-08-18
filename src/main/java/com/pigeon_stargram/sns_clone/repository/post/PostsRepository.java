package com.pigeon_stargram.sns_clone.repository.post;

import com.pigeon_stargram.sns_clone.domain.post.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface PostsRepository extends JpaRepository<Posts, Long> {
    List<Posts> findByUserId(Long userId);

    List<Posts> findByUserIdAndCreatedDateAfter(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
}
