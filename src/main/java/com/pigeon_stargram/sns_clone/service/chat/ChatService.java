package com.pigeon_stargram.sns_clone.service.chat;

import com.pigeon_stargram.sns_clone.domain.chat.ImageChat;
import com.pigeon_stargram.sns_clone.domain.chat.LastMessage;
import com.pigeon_stargram.sns_clone.domain.chat.TextChat;
import com.pigeon_stargram.sns_clone.domain.chat.UnreadChat;
import com.pigeon_stargram.sns_clone.dto.chat.internal.GetUserChatsDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.NewChatDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.SendLastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseChatHistoryDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.UnReadChatCountDto;
import com.pigeon_stargram.sns_clone.event.user.UserConnectEvent;
import com.pigeon_stargram.sns_clone.repository.chat.ImageChatRepository;
import com.pigeon_stargram.sns_clone.repository.chat.LastMessageRepository;
import com.pigeon_stargram.sns_clone.repository.chat.TextChatRepository;
import com.pigeon_stargram.sns_clone.repository.chat.UnreadChatRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.UNREAD_CHAT_COUNT;
import static com.pigeon_stargram.sns_clone.constant.CacheConstants.USER_ID;
import static com.pigeon_stargram.sns_clone.constant.RedisUserConstants.ACTIVE_USERS_KEY_PREFIX;
import static com.pigeon_stargram.sns_clone.service.chat.ChatBuilder.buildSendLastMessageDto;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getCurrentFormattedTime;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;

    private final TextChatRepository textChatRepository;
    private final ImageChatRepository imageChatRepository;

    private final UnreadChatRepository unreadChatRepository;
    private final LastMessageRepository lastMessageRepository;

    private final RedisService redisService;

    public void save(NewChatDto dto){

        if(dto.getIsImage()) imageChatRepository.save(dto.toImageEntity());
        else textChatRepository.save(dto.toTextEntity());

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

    // 테스트 데이터 주입에서만 사용
    public void saveChat(Long fromUserId, Long toUserId, String text) {
        TextChat textChat = TextChat.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .text(text)
                .build();
        textChatRepository.save(textChat);
    }

    public List<ResponseChatHistoryDto> getUserChats(GetUserChatsDto dto) {
        Long user1Id = dto.getUser1Id();
        Long user2Id = dto.getUser2Id();

        List<TextChat> textChats = textChatRepository.findChatsBetweenUsers(user1Id, user2Id);
        List<ImageChat> imageChats = imageChatRepository.findChatsBetweenUsers(user1Id, user2Id);

        List<ResponseChatHistoryDto> chatHistoryDtos = new ArrayList<>();

        chatHistoryDtos.addAll(textChats.stream()
                .map(ChatBuilder::buildResponseChatHistoryDto)
                .collect(Collectors.toList()));

        chatHistoryDtos.addAll(imageChats.stream()
                .map(ChatBuilder::buildResponseChatHistoryDto)
                .collect(Collectors.toList()));

        chatHistoryDtos.sort(Comparator.comparing(ResponseChatHistoryDto::getTime));

        return chatHistoryDtos;
    }


    public Integer increaseUnReadChatCount(Long userId, Long toUserId) {
        // 캐시 키 생성
        String cacheKey = cacheKeyGenerator(UNREAD_CHAT_COUNT, USER_ID, userId.toString());
        String fieldKey = toUserId.toString();

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
        redisService.putValueInHash(cacheKey, fieldKey, count);

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
        redisService.putValueInHash(cacheKey, fieldKey, count);

        return count;
    }

    public void setUnreadChatCount0(Long userId, Long toUserId) {
        // 캐시 키 생성
        String cacheKey = cacheKeyGenerator(UNREAD_CHAT_COUNT, USER_ID, userId.toString());
        String fieldKey = toUserId.toString();

        // 캐시에서 값이 있는지 확인하고, 있다면 값을 0으로 업데이트 (캐시 히트 처리)
        if (redisService.hasFieldInHash(cacheKey, fieldKey)) {
            log.info("캐시 히트: userId={}, toUserId={}", userId, toUserId);
            redisService.putValueInHash(cacheKey, fieldKey, 0);
        } else {
            // 캐시 미스: 캐시에 값이 없다면 DB에서 가져와서 0으로 설정 후, 캐시에도 반영
            log.info("캐시 미스3: userId={}, toUserId={}", userId, toUserId);
            unreadChatRepository.findByUserIdAndToUserId(userId, toUserId)
                    .ifPresent(unreadChat -> {
                        unreadChat.resetCount();
                        unreadChatRepository.save(unreadChat);

                        // 캐시에 저장
                        redisService.putValueInHash(cacheKey, fieldKey, 0);
                    });
        }
    }

    public LastMessageDto setLastMessage(NewChatDto chatMessage) {
        Long user1Id = chatMessage.getFrom();
        Long user2Id = chatMessage.getTo();

        Long[] userIds = sortAndGet(user1Id, user2Id);
        String lastMessageText = chatMessage.getIsImage() ? "사진" : chatMessage.getText();

        LastMessage lastMessageEntity = lastMessageRepository.findByUser1IdAndUser2Id(userIds[0], userIds[1])
                .orElse(new LastMessage(userIds[0], userIds[1], lastMessageText));

        lastMessageEntity.update(lastMessageText);

        //실시간 반영
        LastMessageDto lastMessageDto = new LastMessageDto(lastMessageRepository.save(lastMessageEntity));
        lastMessageDto.setTime(getCurrentFormattedTime());

        return lastMessageDto;
    }

    public LastMessageDto getLastMessage(Long user1Id, Long user2Id) {
        Long[] userIds = sortAndGet(user1Id, user2Id);

        return lastMessageRepository.findByUser1IdAndUser2Id(userIds[0], userIds[1])
                .map(LastMessageDto::new)
                .orElseGet(LastMessageDto::new);
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
