package com.pigeon_stargram.sns_clone.dto.chat.internal;

import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SendLastMessageDto {

    private Long user1Id;
    private Long user2Id;
    private LastMessageDto lastMessageDto;
}
