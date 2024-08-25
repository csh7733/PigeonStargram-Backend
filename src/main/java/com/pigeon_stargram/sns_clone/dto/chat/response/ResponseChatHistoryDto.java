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
@ToString
public class ResponseChatHistoryDto {
    //곧 삭제예정
    private int id;
    private Long from;
    private Long to;
    private String text;
    private String time;

    private Boolean isImage;


}
