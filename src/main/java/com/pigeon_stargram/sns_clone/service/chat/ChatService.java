package com.pigeon_stargram.sns_clone.service.chat;

import com.pigeon_stargram.sns_clone.repository.chat.ChatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;

}
