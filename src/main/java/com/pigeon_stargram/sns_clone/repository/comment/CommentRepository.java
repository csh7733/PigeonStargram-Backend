package com.pigeon_stargram.sns_clone.repository.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
