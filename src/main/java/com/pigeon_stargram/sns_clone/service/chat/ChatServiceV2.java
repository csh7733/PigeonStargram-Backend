package com.pigeon_stargram.sns_clone.service.chat;

import com.pigeon_stargram.sns_clone.domain.chat.Chat;
import com.pigeon_stargram.sns_clone.domain.chat.LastMessage;
import com.pigeon_stargram.sns_clone.domain.chat.UnreadChat;
import com.pigeon_stargram.sns_clone.dto.chat.internal.GetUserChatsDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.NewChatDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.SendLastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseChatHistoriesDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseChatHistoryDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.UnReadChatCountDto;
import com.pigeon_stargram.sns_clone.event.user.UserConnectEvent;
import com.pigeon_stargram.sns_clone.repository.chat.*;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.constant.RedisUserConstants.ACTIVE_USERS_KEY_PREFIX;
import static com.pigeon_stargram.sns_clone.dto.chat.ChatDtoConvertor.toChatHistoryDtos;
import static com.pigeon_stargram.sns_clone.dto.chat.ChatDtoConvertor.toSendLastMessageDto;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.combineHashKeyAndFieldKey;

// 채팅 정보에 대한 캐싱을 적용한 ChatServiceV2 구현체
// Value         | Structure | Key                                 | FieldKey
// ------------- | --------- | ----------------------------------- | --------
// unreadCount   | Hash      | UNREAD_CHAT_COUNT_USER_ID_{userId}  | toUserId   (상대 사용자 ID)
// lastMessage   | Hash      | LAST_MESSAGE_USER_ID_{userId}       | toUserId   (상대 사용자 ID)
// activeUsers   | Hash      | ACTIVE_USERS_KEY_PREFIX_{userId}    | partnerUserId (현재 채팅 중인 사용자 ID)
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatServiceV2 implements ChatService{

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatRepository chatRepository;

    private final UnreadChatRepository unreadChatRepository;
    private final LastMessageRepository lastMessageRepository;

    private final RedisService redisService;

    public void save(NewChatDto dto){
        // 채팅 메시지를 데이터베이스에 저장
        chatRepository.save(dto.toEntity());

        Long user1Id = dto.getFrom();
        Long user2Id = dto.getTo();

        // 상대방이 채팅 중이지 않다면 읽지 않은 메시지 수 증가 및 전송
        if (!isUserChattingWith(user2Id, user1Id)) {
            Integer count = increaseUnReadChatCount(user2Id, user1Id);
            sentUnReadChatCountToUser(user2Id, user1Id, count);
        }

        // LastMessage 업데이트 및 전송
        handleLastMessageUpdate(user1Id, user2Id, dto);
    }

    public void sentUnReadChatCountToUser(Long toUserId, Long fromUserId, Integer count) {
        String channel = getUnReadChatCountChannelName(toUserId);
        UnReadChatCountDto unReadChatCountDto = new UnReadChatCountDto(toUserId,fromUserId, count);
        redisService.publishMessage(channel, unReadChatCountDto); // Redis로 메시지 발행
    }

    public void sentLastMessage(SendLastMessageDto dto) {
        Long user1Id = dto.getUser1Id();
        Long user2Id = dto.getUser2Id();
        LastMessageDto lastMessage = dto.getLastMessageDto();

        // 각 사용자에게 보낼 경로 생성
        String destination1 = buildDestinationPath(user1Id);
        String destination2 = buildDestinationPath(user2Id);

        // 메시지를 각 사용자에게 전송
        messagingTemplate.convertAndSend(destination1, lastMessage);
        messagingTemplate.convertAndSend(destination2, lastMessage);
    }

    public ResponseChatHistoriesDto getUserChats(GetUserChatsDto dto, String lastFetchedTime, Integer size) {
        Long user1Id = dto.getUser1Id();
        Long user2Id = dto.getUser2Id();

        // 마지막 조회 시간을 LocalDateTime 형식으로 변환
        LocalDateTime lastFetchedDateTime = getChatLocalDateTime(lastFetchedTime);

        // 페이지 정보 생성 (size + 1개의 메시지를 내림차순으로 가져옴)
        Pageable pageable = createPageable(size);

        // 데이터베이스에서 size + 1개의 메시지 조회
        List<Chat> chats = chatRepository.findChatsBefore(user1Id, user2Id, lastFetchedDateTime, pageable);

        // size+1개 중에서 size개만 반환하고, size+1번째 데이터가 있으면 hasMoreChat을 true로 설정
        Boolean hasMoreChat = chats.size() > size;
        List<Chat> limitedChats = chats.subList(0, Math.min(chats.size(), size));

        // 메시지를 DTO로 변환
        List<ResponseChatHistoryDto> chatHistoryDtos = toChatHistoryDtos(limitedChats);

        return new ResponseChatHistoriesDto(chatHistoryDtos, hasMoreChat);
    }

    public Integer increaseUnReadChatCount(Long userId,
                                           Long toUserId) {
        // 캐시 키 생성
        String cacheKey = cacheKeyGenerator(UNREAD_CHAT_COUNT, USER_ID, userId.toString());
        String fieldKey = toUserId.toString();

        // 나중에 비동기적으로 DB에 flush 하도록 Write-back 작업을 위한 Sorted Set에 추가
        String dirtyKey = combineHashKeyAndFieldKey(cacheKey, fieldKey);
        redisService.pushToWriteBackSortedSet(dirtyKey);

        // 캐시에서 값 조회 및 증가
        if (redisService.hasFieldInHash(cacheKey, fieldKey)) {
            return incrementUnreadCountInCache(cacheKey, fieldKey, userId, toUserId);
        }

        // 캐시 미스 시 DB에서 값 조회 및 캐시에 저장
        return incrementUnreadCountInDbAndSaveInCache(userId, toUserId, cacheKey, fieldKey);
    }


    public Integer getUnreadChatCount(Long userId, Long toUserId) {
        String cacheKey = cacheKeyGenerator(UNREAD_CHAT_COUNT, USER_ID, userId.toString());
        String fieldKey = toUserId.toString();

        // 캐시에서 값 조회
        if (redisService.hasFieldInHash(cacheKey, fieldKey)) {
            return redisService.getValueFromHash(cacheKey, fieldKey, Integer.class);
        }

        // 캐시 미스 시 DB에서 값 조회 후 캐시에 저장
        return getUnreadCountFromDbAndSaveInCache(userId, toUserId, cacheKey, fieldKey);
    }

    public void setUnreadChatCount0(Long userId, Long toUserId) {
        String cacheKey = cacheKeyGenerator(UNREAD_CHAT_COUNT, USER_ID, userId.toString());
        String fieldKey = toUserId.toString();

        // 나중에 비동기적으로 DB에 flush 하도록 Write-back 작업을 위한 Sorted Set에 추가
        String dirtyKey = combineHashKeyAndFieldKey(cacheKey, fieldKey);
        redisService.pushToWriteBackSortedSet(dirtyKey);

        // 캐시에서 값을 확인하고 0으로 업데이트
        if (redisService.hasFieldInHash(cacheKey, fieldKey)) {
            redisService.putValueInHash(cacheKey, fieldKey, 0, ONE_DAY_TTL);
        } else {
            // 캐시 미스 시 DB에서 값을 가져와 0으로 업데이트 후 캐시에 반영
            updateUnreadCountInDbAndSaveInCache(userId, toUserId, cacheKey, fieldKey);
        }
    }

    public LastMessageDto setLastMessage(NewChatDto chatMessage) {
        Long user1Id = chatMessage.getFrom();
        Long user2Id = chatMessage.getTo();

        // 사용자 ID 정렬 및 캐시 키 생성
        Long[] userIds = sortAndGet(user1Id, user2Id);
        String lastMessageText = chatMessage.getIsImage() ? "사진" : chatMessage.getText();

        String hashKey = cacheKeyGenerator(LAST_MESSAGE, USER_ID, userIds[0].toString());
        String fieldKey = userIds[1].toString();

        // 나중에 비동기적으로 DB에 flush 하도록 Write-back 작업을 위한 Sorted Set에 추가
        String dirtyKey = combineHashKeyAndFieldKey(hashKey, fieldKey);
        redisService.pushToWriteBackSortedSet(dirtyKey);

        // 캐시에서 마지막 메시지를 조회하고, 있으면 업데이트
        if (redisService.hasFieldInHash(hashKey, fieldKey)) {
            return updateLastMessageInCache(hashKey, fieldKey, lastMessageText);
        }

        // 캐시 미스 시 DB에서 마지막 메시지를 조회하고 캐시에 저장
        return updateLastMessageInDbAndCache(userIds, lastMessageText, hashKey, fieldKey);
    }

    public LastMessageDto getLastMessage(Long user1Id, Long user2Id) {
        // 사용자 ID 정렬 (작은 ID가 먼저 오도록)
        Long[] userIds = sortAndGet(user1Id, user2Id);

        // Redis Hash의 키와 필드 키 생성
        String hashKey = cacheKeyGenerator(LAST_MESSAGE, USER_ID, userIds[0].toString());
        String fieldKey = userIds[1].toString();

        // 캐시에서 마지막 메시지 조회
        if (redisService.hasFieldInHash(hashKey, fieldKey)) {
            return redisService.getValueFromHash(hashKey, fieldKey, LastMessageDto.class);
        }

        // 캐시 미스: DB에서 마지막 메시지 조회 후 캐시에 저장
        return getLastMessageFromDbAndCache(userIds, hashKey, fieldKey);
    }

    public Long[] sortAndGet(Long user1Id, Long user2Id) {
        Long[] userIds = {user1Id, user2Id};
        Arrays.sort(userIds);
        return userIds;
    }

    public Boolean isUserChattingWith(Long userId, Long partnerUserId) {
        String activeUsersKey = ACTIVE_USERS_KEY_PREFIX + userId;
        return redisService.hasFieldInHash(activeUsersKey, String.valueOf(partnerUserId));
    }

    @EventListener
    public void handleUserConnect(UserConnectEvent event) {
        Long userId = event.getUserId();
        Long partnerUserId = event.getPartnerUserId();
        if(!userId.equals(partnerUserId)) setUnreadChatCount0(userId,partnerUserId);
    }

    private void handleLastMessageUpdate(Long user1Id, Long user2Id, NewChatDto dto) {
        LastMessageDto lastMessage = setLastMessage(dto);
        SendLastMessageDto sendLastMessageDto = toSendLastMessageDto(user1Id, user2Id, lastMessage);
        publishLastMessage(sendLastMessageDto);
    }

    private void publishLastMessage(SendLastMessageDto dto) {
        String channel = getChatLastMessageChannelName(dto.getUser1Id(), dto.getUser2Id());
        redisService.publishMessage(channel, dto);
    }

    private String buildDestinationPath(Long userId) {
        return "/topic/users/status/" + userId;
    }

    private Pageable createPageable(Integer size) {
        return PageRequest.of(0, size + 1, Sort.by("id").descending());
    }

    private Integer incrementUnreadCountInCache(String cacheKey, String fieldKey, Long userId, Long toUserId) {
        Long newCount = redisService.incrementHashValue(cacheKey, fieldKey, 1);
        return newCount.intValue();
    }

    private Integer incrementUnreadCountInDbAndSaveInCache(Long userId, Long toUserId, String cacheKey, String fieldKey) {
        // DB에서 읽지 않은 채팅 수 조회 또는 새로 생성
        UnreadChat unReadChat = unreadChatRepository.findByUserIdAndToUserId(userId, toUserId)
                .orElse(new UnreadChat(userId, toUserId));

        Integer count = unReadChat.incrementCount();

        // 캐시에 값 저장 (TTL 하루 설정)
        redisService.putValueInHash(cacheKey, fieldKey, count, ONE_DAY_TTL);

        return count;
    }

    private Integer getUnreadCountFromDbAndSaveInCache(Long userId, Long toUserId, String cacheKey, String fieldKey) {
        // DB에서 읽지 않은 메시지 수 조회
        Integer count = unreadChatRepository.findByUserIdAndToUserId(userId, toUserId)
                .map(UnreadChat::getCount)
                .orElse(0);

        // 조회한 값을 캐시에 저장 (TTL: 1일)
        redisService.putValueInHash(cacheKey, fieldKey, count, ONE_DAY_TTL);

        return count;
    }

    private void updateUnreadCountInDbAndSaveInCache(Long userId, Long toUserId, String cacheKey, String fieldKey) {
        unreadChatRepository.findByUserIdAndToUserId(userId, toUserId)
                .ifPresent(unreadChat -> {
                    unreadChat.resetCount();
                    unreadChatRepository.save(unreadChat);

                    // 캐시에 저장
                    redisService.putValueInHash(cacheKey, fieldKey, 0, ONE_DAY_TTL);
                });
    }

    private LastMessageDto updateLastMessageInCache(String hashKey, String fieldKey, String lastMessageText) {
        LastMessageDto lastMessageDto = redisService.getValueFromHash(hashKey, fieldKey, LastMessageDto.class);

        // 캐시에서 가져온 DTO 업데이트
        lastMessageDto.setLastMessage(lastMessageText);
        lastMessageDto.setTime(getCurrentFormattedTime());

        // 캐시 갱신 (TTL: 하루)
        redisService.putValueInHash(hashKey, fieldKey, lastMessageDto, ONE_DAY_TTL);

        return lastMessageDto;
    }

    private LastMessageDto updateLastMessageInDbAndCache(Long[] userIds, String lastMessageText, String hashKey, String fieldKey) {
        // DB에서 마지막 메시지 조회
        LastMessage lastMessageEntity = lastMessageRepository.findByUser1IdAndUser2Id(userIds[0], userIds[1])
                .orElse(new LastMessage(userIds[0], userIds[1], lastMessageText));

        lastMessageEntity.update(lastMessageText);

        // DB에 저장하고, DTO로 변환
        LastMessageDto lastMessageDto = new LastMessageDto(lastMessageRepository.save(lastMessageEntity));
        lastMessageDto.setTime(getCurrentFormattedTime());

        // 캐시에 저장 (TTL: 하루)
        redisService.putValueInHash(hashKey, fieldKey, lastMessageDto, ONE_DAY_TTL);

        return lastMessageDto;
    }

    private LastMessageDto getLastMessageFromDbAndCache(Long[] userIds, String hashKey, String fieldKey) {
        // DB에서 마지막 메시지 조회
        LastMessageDto lastMessageDto = lastMessageRepository.findByUser1IdAndUser2Id(userIds[0], userIds[1])
                .map(LastMessageDto::new)
                .orElseGet(() -> new LastMessageDto(userIds[0], userIds[1]));

        // 조회한 결과를 Redis 캐시에 저장 (TTL: 하루)
        redisService.putValueInHash(hashKey, fieldKey, lastMessageDto, ONE_DAY_TTL);

        return lastMessageDto;
    }

    private String getChatLastMessageChannelName(Long user1Id, Long user2Id) {
        long smallerId = Math.min(user1Id, user2Id);
        long largerId = Math.max(user1Id, user2Id);

        return "lastMessage.chat." + smallerId + "." + largerId;
    }

    private String getUnReadChatCountChannelName(Long toUserId) {
        return "unreadChatCount." + toUserId;
    }
}
