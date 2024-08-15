package com.pigeon_stargram.sns_clone.dto.chat.response;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseOnlineStatusDto {
    private Long userId;
    private String onlineStatus;

    public ResponseOnlineStatusDto(User user) {
        this.userId = user.getId();
        this.onlineStatus = user.getOnlineStatus();
    }
}
