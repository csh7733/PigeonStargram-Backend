package com.pigeon_stargram.sns_clone.repository.post;

import com.pigeon_stargram.sns_clone.domain.post.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);

    Integer countByPostId(Long postId);

}
