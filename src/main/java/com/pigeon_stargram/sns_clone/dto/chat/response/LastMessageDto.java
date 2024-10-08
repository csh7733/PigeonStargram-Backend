package com.pigeon_stargram.sns_clone.dto.chat.response;

import com.pigeon_stargram.sns_clone.domain.chat.LastMessage;
import lombok.*;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LastMessageDto {

    private Long user1Id;
    private Long user2Id;
    private String lastMessage;
    private String time;

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LastMessageDto(LastMessage lastMessage){
        this.user1Id = lastMessage.getUser1Id();
        this.user2Id = lastMessage.getUser2Id();
        this.lastMessage = lastMessage.getLastMessage();
        this.time = formatTime(lastMessage.getModifiedDate());
    }

    public LastMessageDto(Long user1Id,Long user2Id){
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.lastMessage = "대화 기록 없음";
    }
}
