package com.pigeon_stargram.sns_clone.repository.user;

import com.pigeon_stargram.sns_clone.domain.user.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFromUser(User fromUser);
    List<Follow> findByToUser(User toUser);
}
