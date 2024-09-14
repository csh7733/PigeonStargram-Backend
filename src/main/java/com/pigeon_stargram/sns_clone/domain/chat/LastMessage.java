package com.pigeon_stargram.sns_clone.domain.chat;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

/**
 * LastMessage 엔티티는 두 사용자 간의 마지막 채팅 메시지 정보를 나타냅니다.
 * - 사용자 ID와 마지막 메시지를 저장합니다.
 * - RDB의 LAST_MESSAGE 테이블과 매핑됩니다.
 */
@Entity
@Getter
@NoArgsConstructor
@ToString
public class LastMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long user1Id; // 첫 번째 사용자의 ID
    private Long user2Id; // 두 번째 사용자의 ID
    private String lastMessage; // 두 사용자 간의 마지막 메시지

    @Builder
    public LastMessage(Long user1Id, Long user2Id, String lastMessage) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.lastMessage = lastMessage;
    }
    public void update(String lastMessage){
        this.lastMessage = lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
