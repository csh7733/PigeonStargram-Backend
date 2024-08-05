package com.pigeon_stargram.sns_clone.repository.comments;

import com.pigeon_stargram.sns_clone.domain.comments.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long> {
}
