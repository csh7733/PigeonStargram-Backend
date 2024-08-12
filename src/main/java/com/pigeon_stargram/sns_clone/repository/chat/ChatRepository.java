package com.pigeon_stargram.sns_clone.repository.chat;

import com.pigeon_stargram.sns_clone.domain.chat.Chat;
import com.pigeon_stargram.sns_clone.domain.chat.ImageChat;
import com.pigeon_stargram.sns_clone.domain.chat.TextChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM TextChat c WHERE (c.senderId = :user1Id AND c.recipientId = :user2Id) OR (c.senderId = :user2Id AND c.recipientId = :user1Id)")
    List<TextChat> findTextChatsBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    @Query("SELECT c FROM ImageChat c WHERE (c.senderId = :user1Id AND c.recipientId = :user2Id) OR (c.senderId = :user2Id AND c.recipientId = :user1Id)")
    List<ImageChat> findImageChatsBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}