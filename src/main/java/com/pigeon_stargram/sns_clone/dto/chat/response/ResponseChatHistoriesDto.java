package com.pigeon_stargram.sns_clone.dto.chat.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ResponseChatHistoriesDto {
    private List<ResponseChatHistoryDto> chatHistory;
    private Boolean hasMoreChat;

}
