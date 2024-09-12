package com.pigeon_stargram.sns_clone.repository.comment;

import com.pigeon_stargram.sns_clone.domain.comment.CommentLike;
import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId);

    Integer countByCommentId(Long commentId);

    List<CommentLike> findByCommentId(Long commentId);

    Boolean existsByUserIdAndCommentId(Long userId, Long commentId);

    void deleteByUserIdAndCommentId(Long userId, Long commentId);
}
