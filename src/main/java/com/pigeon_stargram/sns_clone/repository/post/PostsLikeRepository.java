package com.pigeon_stargram.sns_clone.repository.post;

import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.post.PostsLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostsLikeRepository extends JpaRepository<PostsLike, Long> {

    Optional<PostsLike> findByUserAndPost(User user, Posts post);

}
