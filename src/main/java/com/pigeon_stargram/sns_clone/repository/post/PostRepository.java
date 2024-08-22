package com.pigeon_stargram.sns_clone.repository.post;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);

    List<Post> findByUserIdAndCreatedDateAfter(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
}
