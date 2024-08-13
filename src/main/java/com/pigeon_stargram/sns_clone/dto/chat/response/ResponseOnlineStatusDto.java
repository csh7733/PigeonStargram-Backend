package com.pigeon_stargram.sns_clone.dto.chat.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseOnlineStatusDto {
    private Long userId;
    private String onlineStatus;

    public ResponseOnlineStatusDto(String onlineStatus){
        this.onlineStatus = onlineStatus;
    }
}
