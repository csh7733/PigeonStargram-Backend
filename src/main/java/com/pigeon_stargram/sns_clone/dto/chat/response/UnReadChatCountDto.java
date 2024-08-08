package com.pigeon_stargram.sns_clone.dto.chat.response;

import com.pigeon_stargram.sns_clone.domain.chat.ImageChat;
import com.pigeon_stargram.sns_clone.domain.chat.TextChat;
import lombok.*;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnReadChatCountDto {
    private Long userId;
    private Integer count;
}
