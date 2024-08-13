package com.pigeon_stargram.sns_clone.dto.chat.response;

import com.pigeon_stargram.sns_clone.domain.chat.LastMessage;
import lombok.*;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LastMessageDto {
    private Long user1Id;
    private Long user2Id;
    private String lastMessage;
    private String time;

    public LastMessageDto(LastMessage lastMessage){
        this.user1Id = lastMessage.getUser1Id();
        this.user2Id = lastMessage.getUser2Id();
        this.lastMessage = lastMessage.getLastMessage();
        this.time = formatTime(lastMessage.getModifiedDate());
    }

    public LastMessageDto(){
        this.lastMessage = "대화 기록 없음";
    }
}
