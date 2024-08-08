package com.pigeon_stargram.sns_clone.domain.chat;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

@Getter
@NoArgsConstructor
@Entity
public class LastMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long user1Id;

    private Long user2Id;

    private String lastMessage;

    @Builder
    public LastMessage(Long user1Id, Long user2Id, String lastMessage) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.lastMessage = lastMessage;
    }
}
