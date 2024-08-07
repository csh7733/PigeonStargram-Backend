package com.pigeon_stargram.sns_clone.repository.chat;

import com.pigeon_stargram.sns_clone.domain.chat.UnReadChat;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface UnReadChatRepository extends JpaRepository<UnReadChat, Long> {
    Optional<UnReadChat> findByUserIdAndToUserId(Long userId, Long toUserId);
}