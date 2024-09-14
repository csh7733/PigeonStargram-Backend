package com.pigeon_stargram.sns_clone.domain.chat;

import com.pigeon_stargram.sns_clone.domain.MongoBaseTimeEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB에 저장되는 채팅 메시지를 나타내는 엔티티 클래스입니다.
 * 각 채팅 메시지는 보낸 사람(sender), 받는 사람(recipient), 메시지 유형(type),
 * 메시지 내용(text), 이미지 경로(imagePath)를 포함합니다.
 */
@Document(collection = "chats")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Chat extends MongoBaseTimeEntity {

    @Id
    private String id;
    private Long senderId;
    private Long recipientId;
    private String type;  // 메시지 유형 ("text" 또는 "image")
    private String text;  // 텍스트 메시지 내용 (텍스트 채팅인 경우)
    private String imagePath;  // 이미지 경로 (이미지 채팅인 경우)

    @Builder
    public Chat(Long senderId, Long recipientId, String type, String text, String imagePath) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.type = type;
        this.text = text;
        this.imagePath = imagePath;
    }
}
