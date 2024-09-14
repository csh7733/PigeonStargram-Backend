package com.pigeon_stargram.sns_clone.dto.chat;

import com.pigeon_stargram.sns_clone.domain.chat.Chat;
import com.pigeon_stargram.sns_clone.dto.chat.internal.GetUserChatsDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.SendLastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseChatHistoryDto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_OPERATION;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.chatFormatTime;

/**
 * Chat 객체와 다양한 DTO 간의 변환을 담당하는 클래스입니다.
 * 채팅 메시지 전송, 조회, 온라인 상태와 관련된 DTO로 변환하는 메서드를 제공합니다.
 */
public class ChatDtoConvertor {
    private ChatDtoConvertor() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    /**
     * 두 사용자 간의 채팅 기록을 조회하기 위한 GetUserChatsDto를 빌드합니다.
     *
     * @param user1Id 첫 번째 사용자의 ID
     * @param user2Id 두 번째 사용자의 ID
     * @return GetUserChatsDto 객체
     */
    public static GetUserChatsDto toGetUserChatsDto(Long user1Id,
                                                    Long user2Id) {
        return GetUserChatsDto.builder()
                .user1Id(user1Id)
                .user2Id(user2Id)
                .build();
    }


    /**
     * 채팅 기록을 응답 형식으로 변환하여 ResponseChatHistoryDto를 빌드합니다.
     *
     * @param chat 변환할 Chat 엔티티
     * @return ResponseChatHistoryDto 객체
     */
    public static ResponseChatHistoryDto toResponseChatHistoryDto(Chat chat) {
        return ResponseChatHistoryDto.builder()
                .id(chat.getId())
                .from(chat.getSenderId())
                .to(chat.getRecipientId())
                .text(chat.getType().equals("image") ? chat.getImagePath() : chat.getText())
                .time(chatFormatTime(chat.getCreatedDate()))
                .isImage(chat.getType().equals("image"))
                .build();
    }

    /**
     * 마지막 메시지를 전송하기 위한 SendLastMessageDto를 빌드합니다.
     *
     * @param user1Id 첫 번째 사용자의 ID
     * @param user2Id 두 번째 사용자의 ID
     * @param lastMessageDto 마지막 메시지를 포함한 DTO
     * @return SendLastMessageDto 객체
     */
    public static SendLastMessageDto toSendLastMessageDto(Long user1Id,
                                                          Long user2Id,
                                                          LastMessageDto lastMessageDto) {
        return SendLastMessageDto.builder()
                .user1Id(user1Id)
                .user2Id(user2Id)
                .lastMessageDto(lastMessageDto)
                .build();
    }

    /**
     * 채팅 엔티티 리스트를 DTO 리스트로 변환합니다.
     *
     * @param limitedChats 제한된 메시지 리스트
     * @return DTO 리스트
     */
    public static List<ResponseChatHistoryDto> toChatHistoryDtos(List<Chat> limitedChats) {
        return limitedChats.stream()
                .map(ChatDtoConvertor::toResponseChatHistoryDto)
                .sorted(Comparator.comparing(ResponseChatHistoryDto::getId))  // ID로 정렬
                .collect(Collectors.toList());
    }
}
