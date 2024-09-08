package com.pigeon_stargram.sns_clone.service.chat;

import com.pigeon_stargram.sns_clone.domain.chat.Chat;
import com.pigeon_stargram.sns_clone.dto.chat.internal.GetUserChatsDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.SendLastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseChatHistoryDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseOnlineStatusDto;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_OPERATION;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.chatFormatTime;
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

    public static ResponseChatHistoryDto buildResponseChatHistoryDto(Chat chat) {
        return ResponseChatHistoryDto.builder()
                .id(chat.getId())
                .from(chat.getSenderId())
                .to(chat.getRecipientId())
                .text(chat.getType().equals("image") ? chat.getImagePath() : chat.getText())
                .time(chatFormatTime(chat.getCreatedDate()))
                .isImage(chat.getType().equals("image"))
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
