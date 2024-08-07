package com.pigeon_stargram.sns_clone.dto.chat.request;


import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetChatHistoryDto {
    private Long user1Id;
    private Long user2Id;
}
