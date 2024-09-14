package com.pigeon_stargram.sns_clone.service.chat;

import com.pigeon_stargram.sns_clone.dto.chat.internal.GetUserChatsDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.NewChatDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.SendLastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseChatHistoriesDto;
import com.pigeon_stargram.sns_clone.event.user.UserConnectEvent;

/**
 * 채팅 관련 비즈니스 로직을 처리하는 Service 인터페이스.
 * 채팅 메시지 저장, 읽지 않은 메시지 관리, 채팅 기록 조회 등 다양한 채팅 관련 기능을 제공합니다.
 */
public interface ChatService {

    /**
     * 새로운 채팅 메시지를 저장하고, 마지막 메시지와 읽지 않은 메시지 수를 갱신합니다.
     *
     * @param dto 저장할 채팅 메시지 정보
     */
    void save(NewChatDto dto);

    /**
     * 특정 사용자에게 읽지 않은 메시지 수를 전송합니다.
     *
     * @param toUserId 읽지 않은 메시지를 전송할 사용자 ID
     * @param fromUserId 메시지를 보낸 사용자 ID
     * @param count 읽지 않은 메시지 수
     */
    void sentUnReadChatCountToUser(Long toUserId, Long fromUserId, Integer count);

    /**
     * 마지막 메시지를 전송합니다.
     *
     * @param dto 마지막 메시지 정보를 포함한 DTO
     */
    void sentLastMessage(SendLastMessageDto dto);

    /**
     * 두 사용자 간의 채팅 기록을 조회합니다.
     *
     * @param dto 두 사용자의 정보를 포함한 DTO
     * @param lastFetchedTime 마지막으로 조회한 시간
     * @param size 조회할 메시지 수
     * @return 채팅 기록 DTO
     */
    ResponseChatHistoriesDto getUserChats(GetUserChatsDto dto, String lastFetchedTime, Integer size);

    /**
     * 읽지 않은 메시지 수를 증가시킵니다.
     *
     * @param userId 메시지를 보낸 사용자 ID
     * @param toUserId 메시지를 받는 사용자 ID
     * @return 증가된 읽지 않은 메시지 수
     */
    Integer increaseUnReadChatCount(Long userId, Long toUserId);

    /**
     * 읽지 않은 메시지 수를 가져옵니다.
     *
     * @param userId 메시지를 보낸 사용자 ID
     * @param toUserId 메시지를 받는 사용자 ID
     * @return 읽지 않은 메시지 수
     */
    Integer getUnreadChatCount(Long userId, Long toUserId);

    /**
     * 읽지 않은 메시지 수를 0으로 설정합니다.
     *
     * @param userId 메시지를 보낸 사용자 ID
     * @param toUserId 메시지를 받는 사용자 ID
     */
    void setUnreadChatCount0(Long userId, Long toUserId);

    /**
     * 마지막 메시지를 설정하고 DTO로 반환합니다.
     *
     * @param chatMessage 저장할 메시지 정보
     * @return 마지막 메시지 DTO
     */
    LastMessageDto setLastMessage(NewChatDto chatMessage);

    /**
     * 두 사용자 간의 마지막 메시지를 조회합니다.
     *
     * @param user1Id 첫 번째 사용자 ID
     * @param user2Id 두 번째 사용자 ID
     * @return 마지막 메시지 DTO
     */
    LastMessageDto getLastMessage(Long user1Id, Long user2Id);

    /**
     * 두 사용자 ID를 정렬하고 반환합니다.
     *
     * @param user1Id 첫 번째 사용자 ID
     * @param user2Id 두 번째 사용자 ID
     * @return 정렬된 사용자 ID 배열
     */
    Long[] sortAndGet(Long user1Id, Long user2Id);


    /**
     * 특정 사용자가 지정된 파트너와 채팅 중인지 확인합니다.
     *
     * @param userId 사용자 ID
     * @param partnerUserId 파트너 사용자 ID
     * @return 채팅 중이면 true, 아니면 false
     */
    Boolean isUserChattingWith(Long userId, Long partnerUserId);
}
