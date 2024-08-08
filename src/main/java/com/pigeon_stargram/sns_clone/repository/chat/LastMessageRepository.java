package com.pigeon_stargram.sns_clone.repository.chat;

import com.pigeon_stargram.sns_clone.domain.chat.LastMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LastMessageRepository extends JpaRepository<LastMessage, Long> {
    Optional<LastMessage> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);
}