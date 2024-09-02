package com.pigeon_stargram.sns_clone.domain.chat;

import com.pigeon_stargram.sns_clone.domain.MongoBaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Document(collection = "text_chats")
public class TextChat  extends MongoBaseTimeEntity {

    @Id
    private String id;
    private Long senderId;
    private Long recipientId;
    private String text;

    @Builder
    public TextChat(Long fromUserId, Long toUserId, String text) {
        this.senderId = fromUserId;
        this.recipientId = toUserId;
        this.text = text;
    }
}
