package com.pigeon_stargram.sns_clone.repository.follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFromUser(User fromUser);
    List<Follow> findByToUser(User toUser);
    Optional<Follow> findByFromUserAndToUser(User fromUser, User toUser);

}
