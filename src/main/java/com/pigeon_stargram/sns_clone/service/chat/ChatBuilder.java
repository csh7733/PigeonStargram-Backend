package com.pigeon_stargram.sns_clone.service.chat;

import com.pigeon_stargram.sns_clone.domain.chat.TextChat;
import com.pigeon_stargram.sns_clone.dto.chat.internal.GetUserChatsDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.SendLastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseChatHistoryDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseOnlineStatusDto;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_OPERATION;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

public class ChatBuilder {
    private ChatBuilder() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    public static GetUserChatsDto buildGetUserChatsDto(Long user1Id,
                                                       Long user2Id) {
        return GetUserChatsDto.builder()
                .user1Id(user1Id)
                .user2Id(user2Id)
                .build();
    }

    public static ResponseChatHistoryDto buildResponseChatHistoryDto(TextChat textChat) {
        return ResponseChatHistoryDto.builder()
                .from(textChat.getSenderId())
                .to(textChat.getRecipientId())
                .text(textChat.getText())
                .time(formatTime(textChat.getCreatedDate()))
                .build();
    }

    public static ResponseOnlineStatusDto buildResponseOnlineStatusDto(Long userId,
                                                                       String onlineStatus) {
        return ResponseOnlineStatusDto.builder()
                .userId(userId)
                .onlineStatus(onlineStatus)
                .build();
    }

    public static SendLastMessageDto buildSendLastMessageDto(Long user1Id,
                                                             Long user2Id,
                                                             LastMessageDto lastMessageDto) {
        return SendLastMessageDto.builder()
                .user1Id(user1Id)
                .user2Id(user2Id)
                .lastMessageDto(lastMessageDto)
                .build();
    }
}
