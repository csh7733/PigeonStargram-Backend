package com.pigeon_stargram.sns_clone.domain.chat;

import com.pigeon_stargram.sns_clone.domain.MongoBaseTimeEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chats")
@ToString
public class Chat extends MongoBaseTimeEntity {

    @Id
    private String id;
    private Long senderId;
    private Long recipientId;
    private String type;  // "text" 또는 "image"로 채팅 유형을 구분
    private String text;  // 텍스트 메시지 (텍스트 채팅인 경우에만 사용)
    private String imagePath;  // 이미지 경로 (이미지 채팅인 경우에만 사용)

    @Builder
    public Chat(Long senderId, Long recipientId, String type, String text, String imagePath) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.type = type;
        this.text = text;
        this.imagePath = imagePath;
    }
}
