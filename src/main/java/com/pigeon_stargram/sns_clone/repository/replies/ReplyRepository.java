package com.pigeon_stargram.sns_clone.repository.replies;

import com.pigeon_stargram.sns_clone.domain.replies.Replies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Replies, Long> {
}
