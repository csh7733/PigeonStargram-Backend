package com.pigeon_stargram.sns_clone.dto.chat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatHistoryDto {
    private int id;
    private Integer from;
    private Integer to;
    private String text;
    private String time;

}
