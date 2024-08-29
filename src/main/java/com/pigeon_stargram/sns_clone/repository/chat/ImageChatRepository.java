package com.pigeon_stargram.sns_clone.repository.chat;

import com.pigeon_stargram.sns_clone.domain.chat.ImageChat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ImageChatRepository extends MongoRepository<ImageChat, String> {

    @Query("{ $or: [ { senderId: ?0, recipientId: ?1 }, { senderId: ?1, recipientId: ?0 } ] }")
    List<ImageChat> findChatsBetweenUsers(Long user1Id, Long user2Id);
}
