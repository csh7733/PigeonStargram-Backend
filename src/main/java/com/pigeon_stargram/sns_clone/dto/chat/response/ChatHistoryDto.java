package com.pigeon_stargram.sns_clone.dto.chat.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatHistoryDto {
    //곧 삭제예정
    private int id;
    private Long from;
    private Long to;
    private String text;
    private String time;

}
