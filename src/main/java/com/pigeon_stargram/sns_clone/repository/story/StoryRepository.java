package com.pigeon_stargram.sns_clone.repository.story;

import com.pigeon_stargram.sns_clone.domain.story.Story;
import com.pigeon_stargram.sns_clone.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findAllByUserAndCreatedDateAfter(User user, LocalDateTime time);
    Boolean existsByUserAndCreatedDateAfter(User user, LocalDateTime createdDate);
}
