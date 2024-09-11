package com.pigeon_stargram.sns_clone.service.chat;

import com.pigeon_stargram.sns_clone.repository.chat.ChatRepository;
import com.pigeon_stargram.sns_clone.repository.chat.UnreadChatRepository;
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

//    public void syncUnreadChatCount(String key) {
//        String[] keys = RedisUtil.parseHashKeyAndFieldKey(key);
//        String hashKey = keys[0];
//        String fieldKey = keys[1];
//
//        Long userId = RedisUtil.parseSuffix(hashKey);
//
//
//        log.info("WriteBack key={}", key);
//        unreadChatRepository.findByUserIdAndToUserId( )
//
//
//    }
}
