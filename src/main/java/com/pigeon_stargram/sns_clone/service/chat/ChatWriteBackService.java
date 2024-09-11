package com.pigeon_stargram.sns_clone.service.chat;

import com.pigeon_stargram.sns_clone.domain.chat.LastMessage;
import com.pigeon_stargram.sns_clone.domain.chat.UnreadChat;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.repository.chat.ChatRepository;
import com.pigeon_stargram.sns_clone.repository.chat.LastMessageRepository;
import com.pigeon_stargram.sns_clone.repository.chat.UnreadChatRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.util.RedisUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ChatWriteBackService {

    private final ChatRepository chatRepository;
    private final UnreadChatRepository unreadChatRepository;
    private final RedisService redisService;
    private final LastMessageRepository lastMessageRepository;

    public void syncUnreadChatCount(String key) {
        log.info("syncUnreadChatCount key={}", key);
        String[] keys = RedisUtil.parseHashKeyAndFieldKey(key);
        String hashKey = keys[0];
        String fieldKey = keys[1];

        Long userId = RedisUtil.parseSuffix(hashKey);
        Long toUserId = Long.valueOf(fieldKey);
        log.info("WriteBack HashKey={}, fieldKey={}", hashKey, fieldKey);

        Integer count = redisService.getValueFromHash(hashKey, fieldKey, Integer.class);

        UnreadChat unreadChat = unreadChatRepository.findByUserIdAndToUserId(userId, toUserId)
                .orElse(new UnreadChat(userId, toUserId));

        log.info("before={}", unreadChat.getCount());
        log.info("after={}", count);

        unreadChat.setCount(count);
        unreadChatRepository.save(unreadChat);
    }

    public void syncLastMessage(String key) {
        log.info("syncLastMessage key={}", key);
        String[] keys = RedisUtil.parseHashKeyAndFieldKey(key);
        String hashKey = keys[0];
        String fieldKey = keys[1];

        Long userId = RedisUtil.parseSuffix(hashKey);
        Long toUserId = Long.valueOf(fieldKey);
        log.info("WriteBack HashKey={}, fieldKey={}", hashKey, fieldKey);

        LastMessageDto lastMessageDto =
                redisService.getValueFromHash(hashKey, fieldKey, LastMessageDto.class);
        String newMessage = lastMessageDto.getLastMessage();

        LastMessage lastMessage = lastMessageRepository.findByUser1IdAndUser2Id(userId, toUserId)
                .orElse(new LastMessage(userId, toUserId, newMessage));

        log.info("before={}", lastMessage);
        log.info("after={}", lastMessageDto);

        lastMessage.setLastMessage(newMessage);
        lastMessageRepository.save(lastMessage);
    }
}
