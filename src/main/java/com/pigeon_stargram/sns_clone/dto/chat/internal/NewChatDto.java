package com.pigeon_stargram.sns_clone.dto.chat.internal;

import com.pigeon_stargram.sns_clone.domain.chat.Chat;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class NewChatDto {

    private Long from;
    private Long to;
    private String text;
    private String time;
    private Boolean isImage;

    public Chat toEntity(){
        if (isImage) {
            return Chat.builder()
                    .senderId(from)
                    .recipientId(to)
                    .type("image")
                    .imagePath(text)
                    .build();
        } else {
            return Chat.builder()
                    .senderId(from)
                    .recipientId(to)
                    .type("text")
                    .text(text)
                    .build();
        }
    }
}
