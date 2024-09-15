package com.pigeon_stargram.sns_clone.service.chat.implV2;

import com.pigeon_stargram.sns_clone.domain.chat.LastMessage;
import com.pigeon_stargram.sns_clone.domain.chat.UnreadChat;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.repository.chat.ChatRepository;
import com.pigeon_stargram.sns_clone.repository.chat.LastMessageRepository;
import com.pigeon_stargram.sns_clone.repository.chat.UnreadChatRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatWriteBackService {

    private final ChatRepository chatRepository;
    private final UnreadChatRepository unreadChatRepository;
    private final RedisService redisService;
    private final LastMessageRepository lastMessageRepository;

    /**
     * Redis에서 가져온 읽지 않은 채팅 카운트를 DB에 동기화합니다.
     * @param key Redis에서 관리하는 읽지 않은 채팅 카운트 키
     */
    public void syncUnreadChatCount(String key) {
        // Redis에서 받은 키를 파싱하여 hashKey와 fieldKey를 분리 (사용자 ID와 상대방 ID)
        String[] keys = RedisUtil.parseHashKeyAndFieldKey(key);
        Long userId = RedisUtil.parseSuffix(keys[0]);  // hashKey로부터 사용자 ID 추출
        Long toUserId = Long.valueOf(keys[1]);  // fieldKey로부터 상대방 사용자 ID 추출

        // Redis에서 읽지 않은 채팅 카운트를 가져옴
        Integer count = redisService.getValueFromHash(keys[0], keys[1], Integer.class);

        // DB에서 해당 사용자와 상대방 사이의 읽지 않은 채팅 정보를 조회 (없다면 새로 생성)
        UnreadChat unreadChat = unreadChatRepository.findByUserIdAndToUserId(userId, toUserId)
                .orElse(new UnreadChat(userId, toUserId));

        // 가져온 카운트로 업데이트 후 DB에 저장
        unreadChat.setCount(count);
        unreadChatRepository.save(unreadChat);
    }

    /**
     * Redis에서 가져온 마지막 메시지를 DB에 동기화합니다.
     * @param key Redis에서 관리하는 마지막 메시지 키
     */
    public void syncLastMessage(String key) {
        // Redis에서 받은 키를 파싱하여 hashKey와 fieldKey를 분리 (사용자 ID와 상대방 ID)
        String[] keys = RedisUtil.parseHashKeyAndFieldKey(key);
        Long userId = RedisUtil.parseSuffix(keys[0]);  // hashKey로부터 사용자 ID 추출
        Long toUserId = Long.valueOf(keys[1]);  // fieldKey로부터 상대방 사용자 ID 추출

        // Redis에서 마지막 메시지 DTO를 가져옴
        LastMessageDto lastMessageDto =
                redisService.getValueFromHash(keys[0], keys[1], LastMessageDto.class);
        String newMessage = lastMessageDto.getLastMessage();

        // DB에서 해당 사용자와 상대방 사이의 마지막 메시지를 조회 (없다면 새로 생성)
        LastMessage lastMessage = lastMessageRepository.findByUser1IdAndUser2Id(userId, toUserId)
                .orElse(new LastMessage(userId, toUserId, newMessage));


        // 마지막 메시지를 업데이트 후 DB에 저장
        lastMessage.setLastMessage(newMessage);
        lastMessageRepository.save(lastMessage);
    }
}
