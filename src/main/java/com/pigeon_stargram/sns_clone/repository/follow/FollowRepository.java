package com.pigeon_stargram.sns_clone.repository.follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    List<Follow> findBySenderId(Long senderId);

    List<Follow> findByRecipientId(Long recipientId);

    Boolean existsBySenderIdAndRecipientId(Long senderId, Long recipientId);

    void deleteBySenderIdAndRecipientId(Long senderId, Long recipientId);
}
