package com.pigeon_stargram.sns_clone.dto.chat.response;

import com.pigeon_stargram.sns_clone.domain.chat.LastMessage;
import lombok.*;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LastMessageDto {
    private String lastMessage;
    private String time;

    public LastMessageDto(LastMessage lastMessage){
        this.lastMessage = lastMessage.getLastMessage();
        this.time = formatTime(lastMessage.getCreatedDate());
    }
}
