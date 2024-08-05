package com.pigeon_stargram.sns_clone.repository.posts;

import com.pigeon_stargram.sns_clone.domain.posts.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PostsRepository extends JpaRepository<Posts, Long> {
}
