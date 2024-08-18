package com.pigeon_stargram.sns_clone.repository.chat;

import com.pigeon_stargram.sns_clone.domain.chat.UnreadChat;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface UnreadChatRepository extends JpaRepository<UnreadChat, Long> {
    Optional<UnreadChat> findByUserIdAndToUserId(Long userId, Long toUserId);
}