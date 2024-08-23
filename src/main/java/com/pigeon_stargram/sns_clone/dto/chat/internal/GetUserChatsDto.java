package com.pigeon_stargram.sns_clone.dto.chat.internal;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class GetUserChatsDto {

    private Long user1Id;
    private Long user2Id;
}
