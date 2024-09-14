package com.pigeon_stargram.sns_clone.domain.chat;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * UnreadChat 엔티티는 특정 사용자에게 읽지 않은 채팅 메시지의 수를 나타냅니다.
 * - 사용자 간의 읽지 않은 메시지 수를 저장합니다.
 * - RDB의 UNREAD_CHAT 테이블과 매핑됩니다.
 */
@Entity
@Getter
@NoArgsConstructor
@ToString
public class UnreadChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // 읽지 않은 메시지를 보낸 사용자의 ID
    private Long toUserId; // 읽지 않은 메시지를 받은 사용자의 ID
    private Integer count; // 읽지 않은 메시지의 수

    public UnreadChat(Long userId, Long toUserId) {
        this.userId = userId;
        this.toUserId = toUserId;
        this.count = 0;
    }

    @Builder
    public UnreadChat(Long userId, Long toUserId, Integer count) {
        this.userId = userId;
        this.toUserId = toUserId;
        this.count = count;
    }

    public Integer incrementCount() {
        this.count += 1;

        return this.count;
    }

    public void resetCount() {
        this.count = 0;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
