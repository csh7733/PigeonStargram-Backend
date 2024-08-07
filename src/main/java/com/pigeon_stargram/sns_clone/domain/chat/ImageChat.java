package com.pigeon_stargram.sns_clone.domain.chat;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("IMAGE")
public class ImageChat extends Chat {

    private String imagePath;

    @Builder
    public ImageChat(Long fromUserId, Long toUserId, String imagePath) {
        super(fromUserId, toUserId);
        this.imagePath = imagePath;
    }
}
