package com.pigeon_stargram.sns_clone.dto.chat.request;

import lombok.*;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestOnlineStatusDto {
    private String onlineStatus;
}
