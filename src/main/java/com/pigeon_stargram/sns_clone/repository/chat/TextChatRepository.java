package com.pigeon_stargram.sns_clone.repository.chat;

import com.pigeon_stargram.sns_clone.domain.chat.TextChat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TextChatRepository extends MongoRepository<TextChat, String> {

    @Query("{ $or: [ { senderId: ?0, recipientId: ?1 }, { senderId: ?1, recipientId: ?0 } ] }")
    List<TextChat> findChatsBetweenUsers(Long user1Id, Long user2Id);
}
