package com.pigeon_stargram.sns_clone.repository.chat;

import com.pigeon_stargram.sns_clone.domain.chat.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String> {

    /**
     * 두 사용자 간의 채팅 기록을 조회하는 메서드입니다.
     *
     * 주어진 두 사용자 ID와 마지막으로 가져온 시점을 기준으로,
     * 그 이전에 생성된 채팅 기록을 페이징 처리하여 반환합니다.
     *
     * @param user1Id 첫 번째 사용자 ID
     * @param user2Id 두 번째 사용자 ID
     * @param lastFetchedTime 조회할 기준 시간 (해당 시간 이전의 채팅을 조회)
     * @param pageable 페이징 정보 (페이지 크기와 정렬)
     * @return 두 사용자 간의 특정 시점 이전의 채팅 목록
     */
    @Query("{ $or: [ { senderId: ?0, recipientId: ?1 }, { senderId: ?1, recipientId: ?0 } ], createdDate: { $lt: ?2 } }")
    List<Chat> findChatsBefore(Long user1Id, Long user2Id, LocalDateTime lastFetchedTime, Pageable pageable);
}
