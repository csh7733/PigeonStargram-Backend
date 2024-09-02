package com.pigeon_stargram.sns_clone.domain.chat;

import com.pigeon_stargram.sns_clone.domain.MongoBaseTimeEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Getter
@NoArgsConstructor
@Document(collection = "image_chats")
public class ImageChat extends MongoBaseTimeEntity {

    @Id
    private String id;
    private Long senderId;
    private Long recipientId;
    private String imagePath;

    @Builder
    public ImageChat(Long fromUserId, Long toUserId, String imagePath) {
        this.senderId = fromUserId;
        this.recipientId = toUserId;
        this.imagePath = imagePath;
    }
}
