package com.pigeon_stargram.sns_clone.domain.chat;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class UnreadChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long toUserId;

    private Integer count;



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
        if (this.count >= 99) {
            this.count = 99;
        }
        this.count += 1;

        return this.count;
    }

    public void resetCount() {
        this.count = 0;
    }
}
