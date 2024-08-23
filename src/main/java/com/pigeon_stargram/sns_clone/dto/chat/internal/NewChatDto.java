package com.pigeon_stargram.sns_clone.dto.chat.internal;

import com.pigeon_stargram.sns_clone.domain.chat.ImageChat;
import com.pigeon_stargram.sns_clone.domain.chat.TextChat;
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

    public TextChat toTextEntity(){
        return TextChat.builder()
                .fromUserId(from)
                .toUserId(to)
                .text(text)
                .build();
    }

    public ImageChat toImageEntity(){
        return ImageChat.builder()
                .fromUserId(from)
                .toUserId(to)
                .imagePath(text)
                .build();
    }
}
