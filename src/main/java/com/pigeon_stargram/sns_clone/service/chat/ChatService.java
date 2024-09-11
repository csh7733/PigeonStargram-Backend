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
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.constant.RedisUserConstants.ACTIVE_USERS_KEY_PREFIX;
import static com.pigeon_stargram.sns_clone.service.chat.ChatBuilder.buildSendLastMessageDto;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.hashWriteBackKeyGenerator;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatRepository chatRepository;

    private final UnreadChatRepository unreadChatRepository;
    private final LastMessageRepository lastMessageRepository;

    private final RedisService redisService;

    public void save(NewChatDto dto){

        chatRepository.save(dto.toEntity());

        Long user1Id = dto.getFrom();
        Long user2Id = dto.getTo();

        if (!isUserChattingWith(user2Id, user1Id)) {
            Integer count = increaseUnReadChatCount(user2Id, user1Id);
            sentUnReadChatCountToUser(user2Id, user1Id, count);
        }

        LastMessageDto lastMessage = setLastMessage(dto);
        SendLastMessageDto sendLastMessageDto =
                buildSendLastMessageDto(user1Id, user2Id, lastMessage);

        publishLastMessage(sendLastMessageDto);
    }

    private void publishLastMessage(SendLastMessageDto dto) {
        String channel = getChatLastMessageChannelName(dto.getUser1Id(), dto.getUser2Id());
        redisService.publishMessage(channel, dto);
    }

    public void sentUnReadChatCountToUser(Long toUserId, Long fromUserId, Integer count) {
        String channel = getUnReadChatCountChannelName(toUserId);
        UnReadChatCountDto unReadChatCountDto = new UnReadChatCountDto(toUserId,fromUserId, count);
        redisService.publishMessage(channel, unReadChatCountDto);
    }

    public void sentLastMessage(SendLastMessageDto dto) {
        Long user1Id = dto.getUser1Id();
        Long user2Id = dto.getUser2Id();
        LastMessageDto lastMessage = dto.getLastMessageDto();

        String destination1 = "/topic/users/status/" + user1Id;
        String destination2 = "/topic/users/status/" + user2Id;
        messagingTemplate.convertAndSend(destination1, lastMessage);
        messagingTemplate.convertAndSend(destination2, lastMessage);
    }

    public ResponseChatHistoriesDto getUserChats(GetUserChatsDto dto, String lastFetchedTime, Integer size) {
        Long user1Id = dto.getUser1Id();
        Long user2Id = dto.getUser2Id();

        LocalDateTime lastFetchedDateTime = getChatLocalDateTime(lastFetchedTime);

        // size+1개의 데이터를 가져옴
        Pageable pageable = PageRequest.of(0, size + 1, Sort.by("id").descending());
        List<Chat> chats = chatRepository.findChatsBefore(user1Id, user2Id, lastFetchedDateTime, pageable);

        // size+1개 중에서 size개만 반환하고, size+1번째 데이터가 있으면 hasMoreChat을 true로 설정
        Boolean hasMoreChat = chats.size() > size;
        List<Chat> limitedChats = chats.subList(0, Math.min(chats.size(), size));

        // 최대 size개의 메시지만 DTO로 변환
        List<ResponseChatHistoryDto> chatHistoryDtos = limitedChats.stream()
                .map(ChatBuilder::buildResponseChatHistoryDto)
                .sorted(Comparator.comparing(ResponseChatHistoryDto::getId))
                .collect(Collectors.toList());

        return new ResponseChatHistoriesDto(chatHistoryDtos, hasMoreChat);
    }


    public Integer increaseUnReadChatCount(Long userId,
                                           Long toUserId) {
        // 캐시 키 생성
        String cacheKey = cacheKeyGenerator(UNREAD_CHAT_COUNT, USER_ID, userId.toString());
        String fieldKey = toUserId.toString();

        // write back set에 추가
        String dirtyKey = hashWriteBackKeyGenerator(cacheKey, fieldKey);
        redisService.pushToWriteBackSet(dirtyKey);

        // Redis Hash에서 해당 값이 있는지 확인하고, 있으면 값을 증가시킵니다.
        if (redisService.hasFieldInHash(cacheKey, fieldKey)) {
            log.info("캐시 히트: userId={}, toUserId={}", userId, toUserId);
            Long newCount = redisService.incrementHashValue(cacheKey, fieldKey, 1);
            return newCount.intValue();
        }

        // 캐시 미스: DB에서 가져와서 값을 증가시키고, 캐시에 저장합니다.
        log.info("캐시 미스1: userId={}, toUserId={}", userId, toUserId);
        UnreadChat unReadChat = unreadChatRepository.findByUserIdAndToUserId(userId, toUserId)
                .orElse(new UnreadChat(userId, toUserId));

        Integer count = unReadChat.incrementCount();

        // DB에 저장
        unreadChatRepository.save(unReadChat);

        // 캐시에 저장
        // TTL은 하루로 설정
        redisService.putValueInHash(cacheKey, fieldKey, count, ONE_DAY_TTL);

        return count;
    }


    public Integer getUnreadChatCount(Long userId, Long toUserId) {
        // 캐시 키 생성
        String cacheKey = cacheKeyGenerator(UNREAD_CHAT_COUNT, USER_ID, userId.toString());
        String fieldKey = toUserId.toString();

        // Redis Hash에서 조회
        if (redisService.hasFieldInHash(cacheKey, fieldKey)) {
            log.info("캐시 히트: userId={}, toUserId={}", userId, toUserId);
            return redisService.getValueFromHash(cacheKey, fieldKey, Integer.class);
        }

        // 캐시 미스: DB에서 가져와서 캐시에 저장
        log.info("캐시 미스2: userId={}, toUserId={}", userId, toUserId);
        Integer count = unreadChatRepository.findByUserIdAndToUserId(userId, toUserId)
                .map(UnreadChat::getCount)
                .orElse(0);

        // 조회한 결과를 Redis Hash에 저장
        redisService.putValueInHash(cacheKey, fieldKey, count, ONE_DAY_TTL);

        return count;
    }

    public void setUnreadChatCount0(Long userId, Long toUserId) {
        // 캐시 키 생성
        String cacheKey = cacheKeyGenerator(UNREAD_CHAT_COUNT, USER_ID, userId.toString());
        String fieldKey = toUserId.toString();

        // 캐시에서 값이 있는지 확인하고, 있다면 값을 0으로 업데이트 (캐시 히트 처리)
        // TTL은 하루로 설정
        if (redisService.hasFieldInHash(cacheKey, fieldKey)) {
            log.info("캐시 히트: userId={}, toUserId={}", userId, toUserId);
            redisService.putValueInHash(cacheKey, fieldKey, 0, ONE_DAY_TTL);
        } else {
            // 캐시 미스: 캐시에 값이 없다면 DB에서 가져와서 0으로 설정 후, 캐시에도 반영
            log.info("캐시 미스3: userId={}, toUserId={}", userId, toUserId);
            unreadChatRepository.findByUserIdAndToUserId(userId, toUserId)
                    .ifPresent(unreadChat -> {
                        unreadChat.resetCount();
                        unreadChatRepository.save(unreadChat);

                        // 캐시에 저장
                        // TTL은 하루로 설정
                        redisService.putValueInHash(cacheKey, fieldKey, 0, ONE_DAY_TTL);
                    });
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

        LastMessageDto lastMessageDto;

        // 캐시에서 조회
        if (redisService.hasFieldInHash(hashKey, fieldKey)) {
            log.info("캐시 히트: user1Id={}, user2Id={}", userIds[0], userIds[1]);
            lastMessageDto = redisService.getValueFromHash(hashKey, fieldKey, LastMessageDto.class);

            // 캐시에서 가져온 DTO를 업데이트
            lastMessageDto.setLastMessage(lastMessageText);
            lastMessageDto.setTime(getCurrentFormattedTime());

            // 캐시 갱신
            // TTL은 하루로 설정
            redisService.putValueInHash(hashKey, fieldKey, lastMessageDto, ONE_DAY_TTL);

        } else {
            // 캐시 미스
            log.info("캐시 미스: user1Id={}, user2Id={}", userIds[0], userIds[1]);

            // DB 조회 및 생성
            LastMessage lastMessageEntity = lastMessageRepository.findByUser1IdAndUser2Id(userIds[0], userIds[1])
                    .orElse(new LastMessage(userIds[0], userIds[1], lastMessageText));
            lastMessageEntity.update(lastMessageText);

            // DB 저장 후 DTO 생성
            lastMessageDto = new LastMessageDto(lastMessageRepository.save(lastMessageEntity));
            lastMessageDto.setTime(getCurrentFormattedTime());

            // 캐시에 저장
            // TTL은 하루로 설정
            redisService.putValueInHash(hashKey, fieldKey, lastMessageDto, ONE_DAY_TTL);
        }

        return lastMessageDto;
    }

    public LastMessageDto getLastMessage(Long user1Id, Long user2Id) {
        // 사용자 ID 정렬
        Long[] userIds = sortAndGet(user1Id, user2Id);

        // Redis Hash의 키와 필드 키 생성
        String hashKey = cacheKeyGenerator(LAST_MESSAGE, USER_ID, userIds[0].toString());
        String fieldKey = userIds[1].toString();

        // 캐시에서 조회
        if (redisService.hasFieldInHash(hashKey, fieldKey)) {
            log.info("캐시 히트: user1Id={}, user2Id={}", userIds[0], userIds[1]);
            return redisService.getValueFromHash(hashKey, fieldKey, LastMessageDto.class);
        }

        // 캐시 미스: DB에서 조회하고 캐시에 저장
        log.info("캐시 미스: user1Id={}, user2Id={}", userIds[0], userIds[1]);
        LastMessageDto lastMessageDto = lastMessageRepository.findByUser1IdAndUser2Id(userIds[0], userIds[1])
                .map(LastMessageDto::new)
                .orElseGet(() -> new LastMessageDto(userIds[0], userIds[1]));

        // 조회한 결과를 Redis Hash에 저장
        // TTL은 하루로 설정
        redisService.putValueInHash(hashKey, fieldKey, lastMessageDto, ONE_DAY_TTL);

        return lastMessageDto;
    }


    @EventListener
    public void handleUserConnect(UserConnectEvent event) {
        Long userId = event.getUserId();
        Long partnerUserId = event.getPartnerUserId();
        if(!userId.equals(partnerUserId)) setUnreadChatCount0(userId,partnerUserId);
    }

    public Long[] sortAndGet(Long user1Id, Long user2Id) {
        Long[] userIds = {user1Id, user2Id};
        Arrays.sort(userIds);
        return userIds;
    }

    private String getChatLastMessageChannelName(Long user1Id, Long user2Id) {
        long smallerId = Math.min(user1Id, user2Id);
        long largerId = Math.max(user1Id, user2Id);

        return "lastMessage.chat." + smallerId + "." + largerId;
    }

    private String getUnReadChatCountChannelName(Long toUserId) {
        return "unreadChatCount." + toUserId;
    }

    public boolean isUserChattingWith(Long userId, Long partnerUserId) {
        String activeUsersKey = ACTIVE_USERS_KEY_PREFIX + userId;
        return redisService.hasFieldInHash(activeUsersKey, String.valueOf(partnerUserId));
    }
}
