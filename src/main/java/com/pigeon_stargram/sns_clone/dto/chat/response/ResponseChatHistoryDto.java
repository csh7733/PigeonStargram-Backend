package com.pigeon_stargram.sns_clone.dto.chat.response;

import com.pigeon_stargram.sns_clone.domain.chat.ImageChat;
import com.pigeon_stargram.sns_clone.domain.chat.TextChat;
import lombok.*;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ResponseChatHistoryDto {
    //곧 삭제예정
    private int id;
    private Long from;
    private Long to;
    private String text;
    private String time;
    private Boolean isImage;

    public ResponseChatHistoryDto(TextChat textChat) {
        this.from = textChat.getSenderId();
        this.to = textChat.getRecipientId();
        this.text = textChat.getText();
        this.time = formatTime(textChat.getCreatedDate());
        this.isImage = false;
    }

    public ResponseChatHistoryDto(ImageChat imageChat) {
        this.from = imageChat.getSenderId();
        this.to = imageChat.getRecipientId();
        this.text = imageChat.getImagePath();
        this.time = formatTime(imageChat.getCreatedDate());
        this.isImage = true;
    }
}
