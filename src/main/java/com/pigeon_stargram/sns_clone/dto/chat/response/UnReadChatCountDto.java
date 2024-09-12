package com.pigeon_stargram.sns_clone.dto.chat.response;

import lombok.*;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UnReadChatCountDto {
    private Long toUserId;
    private Long userId;
    private Integer count;
}
