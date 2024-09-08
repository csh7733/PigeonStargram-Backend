package com.pigeon_stargram.sns_clone.dto.chat.response;

import lombok.*;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ResponseChatHistoryDto {
    private String id;
    private Long from;
    private Long to;
    private String text;
    private String time;
    private Boolean isImage;


}
