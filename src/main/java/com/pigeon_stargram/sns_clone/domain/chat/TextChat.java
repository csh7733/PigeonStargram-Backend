package com.pigeon_stargram.sns_clone.domain.chat;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("TEXT")
public class TextChat extends Chat {

    private String text;

    @Builder
    public TextChat(Long fromUserId, Long toUserId, String text) {
        super(fromUserId, toUserId);
        this.text = text;
    }
}
