package com.pigeon_stargram.sns_clone.service.chat;

import com.pigeon_stargram.sns_clone.domain.chat.ImageChat;
import com.pigeon_stargram.sns_clone.domain.chat.LastMessage;
import com.pigeon_stargram.sns_clone.domain.chat.TextChat;
import com.pigeon_stargram.sns_clone.domain.chat.UnReadChat;
import com.pigeon_stargram.sns_clone.dto.chat.NewChatDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ChatHistoryDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.event.UserConnectEvent;
import com.pigeon_stargram.sns_clone.repository.chat.ChatRepository;
import com.pigeon_stargram.sns_clone.repository.chat.LastMessageRepository;
import com.pigeon_stargram.sns_clone.repository.chat.UnReadChatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final UnReadChatRepository unReadChatRepository;
    private final LastMessageRepository lastMessageRepository;

    public void save(NewChatDto request){
        if(request.getIsImage()) chatRepository.save(request.toImageEntity());
        else chatRepository.save(request.toTextEntity());
    }

    public void saveChat(Long fromUserId, Long toUserId, String text) {
        TextChat textChat = TextChat.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .text(text)
                .build();
        chatRepository.save(textChat);
    }

    /**
     * TODO : 이미지 채팅 구현
     */
    public void saveImageChat(Long fromUserId, Long toUserId, String imagePath) {
        ImageChat imageChat = ImageChat.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .imagePath(imagePath)
                .build();
        chatRepository.save(imageChat);
    }

    public List<ChatHistoryDto> getUserChats(Long user1Id, Long user2Id) {
        List<TextChat> chatHistories = chatRepository.findTextChatsBetweenUsers(user1Id, user2Id);
        return chatHistories.stream()
                .map(ChatHistoryDto::new)
                .collect(Collectors.toList());
    }

    public Integer increaseUnReadChatCount(Long userId,Long toUserId){
        UnReadChat unReadChat = unReadChatRepository.findByUserIdAndToUserId(userId, toUserId)
                .orElse(UnReadChat.builder()
                        .userId(userId)
                        .toUserId(toUserId)
                        .build());

        Integer count = unReadChat.incrementCount();
        unReadChatRepository.save(unReadChat);
        return count;
    }

    public Integer getUnreadChatCount(Long userId, Long toUserId) {
        return unReadChatRepository.findByUserIdAndToUserId(userId, toUserId)
                .map(UnReadChat::getCount)
                .orElse(0);
    }

    public void setUnreadChatCount0(Long userId, Long toUserId) {
        unReadChatRepository.findByUserIdAndToUserId(userId, toUserId)
                .ifPresent(unReadChat -> {
                    unReadChat.resetCount();
                    unReadChatRepository.save(unReadChat);
                });
    }

    public void setLastMessage(NewChatDto request) {
        Long user1Id = request.getFrom();
        Long user2Id = request.getTo();

        Long[] userIds = sortAndGet(user1Id, user2Id);

        String lastMessage = request.getText();
        lastMessageRepository.save(new LastMessage(userIds[0], userIds[1], lastMessage));
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
        setUnreadChatCount0(userId,partnerUserId);
    }

    private static Long[] sortAndGet(Long user1Id, Long user2Id) {
        Long[] userIds = {user1Id, user2Id};
        Arrays.sort(userIds);
        return userIds;
    }
}
