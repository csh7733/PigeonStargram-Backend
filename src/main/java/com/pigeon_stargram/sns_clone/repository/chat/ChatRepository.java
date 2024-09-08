package com.pigeon_stargram.sns_clone.repository.chat;

import com.pigeon_stargram.sns_clone.domain.chat.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String> {

    @Query("{ $or: [ { senderId: ?0, recipientId: ?1 }, { senderId: ?1, recipientId: ?0 } ], createdDate: { $lt: ?2 } }")
    List<Chat> findChatsBefore(Long user1Id, Long user2Id, LocalDateTime lastFetchedTime, Pageable pageable);
}
