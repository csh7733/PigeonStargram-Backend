package com.pigeon_stargram.sns_clone.dto.chat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatHistoryDto {
    private int id;
    private String from;
    private String to;
    private String text;
    private String time;

}
